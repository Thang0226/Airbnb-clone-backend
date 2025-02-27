package com.codegym.service.house;


import com.codegym.model.House;
import com.codegym.model.HouseImage;
import com.codegym.model.User;
import com.codegym.model.dto.HouseDTO;
import com.codegym.repository.IHouseRepository;
import com.codegym.repository.IUserRepository;
import com.codegym.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class HouseService implements IHouseService {

    @Autowired
    private IHouseRepository houseRepository;

    @Autowired
    private IUserRepository userRepository;

    @Value("${FILE_UPLOAD}")
    private String UPLOAD_DIR;

    @Override
    public List<House> findAll() {
        return houseRepository.findAll();
    }

    @Override
    public Optional<House> findById(Long id) {
        return houseRepository.findById(id);
    }

    @Override
    public void save(House object) {
        houseRepository.save(object);
    }

    @Override
    public void deleteById(Long id) {
        houseRepository.deleteById(id);
    }

    @Override
    public List<House> searchHousesAsc(String address, LocalDate checkIn, LocalDate checkOut, Integer minBedrooms, Integer minBathrooms, Integer minPrice, Integer maxPrice) {
        List<House> houses;
        if (checkIn == null) {
            houses = houseRepository.searchHousesAsc(address, null, null, minBedrooms, minBathrooms, minPrice, maxPrice);
        } else if (checkOut == null) {
            houses = houseRepository.searchHousesAsc(address, checkIn, checkIn.plusDays(1), minBedrooms, minBathrooms, minPrice, maxPrice);
        } else {
            houses = houseRepository.searchHousesAsc(address, checkIn, checkOut, minBedrooms, minBathrooms, minPrice, maxPrice);
        }
        return houses;
    }

    @Override
    public List<House> searchHousesDesc(String address, LocalDate checkIn, LocalDate checkOut, Integer minBedrooms, Integer minBathrooms, Integer minPrice, Integer maxPrice) {
        List<House> houses;
        if (checkIn == null) {
            houses = houseRepository.searchHousesDesc(address, null, null, minBedrooms, minBathrooms, minPrice, maxPrice);
        } else if (checkOut == null) {
            houses = houseRepository.searchHousesDesc(address, checkIn, checkIn.plusDays(1), minBedrooms, minBathrooms, minPrice, maxPrice);
        } else {
            houses = houseRepository.searchHousesDesc(address, checkIn, checkOut, minBedrooms, minBathrooms, minPrice, maxPrice);
        }
        return houses;
    }

    @Override
    public List<House> findHousesByHostId(Long id) {
        return houseRepository.findHousesByHost_Id(id);
    }

    // Update House Details
    @Override
    public HouseDTO updateHouseDetails(Long id, HouseDTO houseDTO) {
        Optional<House> optionalHouse = findById(id);
        if (optionalHouse.isEmpty()) {
            return null; // 404 in controller
        }

        House house = optionalHouse.get();

        // Update fields, keep existing if null
        house.setHouseName(houseDTO.getHouseName() != null ? houseDTO.getHouseName() : house.getHouseName());
        house.setAddress(houseDTO.getAddress() != null ? houseDTO.getAddress() : house.getAddress());
        house.setBedrooms(houseDTO.getBedrooms() != null ? houseDTO.getBedrooms() : house.getBedrooms());
        house.setBathrooms(houseDTO.getBathrooms() != null ? houseDTO.getBathrooms() : house.getBathrooms());
        house.setDescription(houseDTO.getDescription() != null ? houseDTO.getDescription() : house.getDescription());
        house.setPrice(houseDTO.getPrice() != null ? houseDTO.getPrice() : house.getPrice());

        // Update host
        if (houseDTO.getUsername() != null) {
            User host = userRepository.findByUsername(houseDTO.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("Username not found"));
            house.setHost(host);
        }

        // Save and convert to DTO
        houseRepository.save(house);
        return convertToDTO(house);
    }

    @Override
    public void removeHouseImage(Long houseId, Long imageId){
        Optional<House> optionalHouse = findById(houseId);
        if (optionalHouse.isEmpty()) {
            return; // 404 in controller
        }
        House house = optionalHouse.get();

        HouseImage image = house.getHouseImages().stream()
                .filter(img -> img.getId().equals(imageId))
                .findFirst()
                .orElse(null); // 404 in controller

        if (image == null) {
            return;
        }

        // Delete image file from storage
        Path imagePath = Paths.get(UPLOAD_DIR, image.getFileName());
        try {
            Files.deleteIfExists(imagePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete image file: " + e.getMessage());
        }

        house.getHouseImages().remove(image);
        // If no images remain, add default.png
        if (house.getHouseImages().isEmpty()) {
            try {
                addDefaultImage(house);
            } catch (IOException e) {
                throw new RuntimeException("Failed to add default image: " + e.getMessage());
            }
        }
        houseRepository.save(house);
    }

    @Override
    public void addHouseImages(Long houseId, List<MultipartFile> images)  {
        Optional<House> optionalHouse = findById(houseId);
        if (optionalHouse.isEmpty()) {
            return; // 404 in controller
        }
        House house = optionalHouse.get();

        // If only default.png exists, remove it and clear images
        if (house.getHouseImages().size() == 1 && house.getHouseImages().get(0).getFileName().equals("default.png")) {
            Path defaultPath = Paths.get(UPLOAD_DIR, "default.png");
            try {
                Files.deleteIfExists(defaultPath); // Remove default.png from storage
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete default image: " + e.getMessage());
            }
            house.getHouseImages().clear();
        }

        // Add new valid images (PNG/JPEG)
        for (MultipartFile image : images) {
            String contentType = image.getContentType();
            if (contentType != null && (contentType.equals("image/jpeg") || contentType.equals("image/png"))) {
                String fileName = image.getOriginalFilename();
                Path filePath = Paths.get(UPLOAD_DIR, fileName);
                try {
                    Files.createDirectories(filePath.getParent()); // Create folder if it doesn’t exist
                    Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING); // Save new image
                }
                catch(IOException e) {
                    throw new RuntimeException("Failed to save image: " + e.getMessage());
                }
                // Add new image to house
                HouseImage houseImage = new HouseImage();
                houseImage.setFileName(fileName);
                houseImage.setHouse(house);
                house.getHouseImages().add(houseImage);
            }
        }

        houseRepository.save(house);
    }




    public ResponseEntity<String> validateInput(HouseDTO houseDTO) {
        if (houseDTO.getBedrooms() == null || houseDTO.getBedrooms() < 1 || houseDTO.getBedrooms() > 10) {
            return ResponseEntity.badRequest().body("Bedrooms must be between 1 and 10");
        }
        if (houseDTO.getBathrooms() == null || houseDTO.getBathrooms() < 1 || houseDTO.getBathrooms() > 3) {
            return ResponseEntity.badRequest().body("Bathrooms must be between 1 and 3");
        }
        if (houseDTO.getPrice() == null || houseDTO.getPrice() < 100000) {
            return ResponseEntity.badRequest().body("Price must be at least 100,000 VND");
        }
        return null;
    }
    private void addDefaultImage(House house) throws IOException {
        String defaultFileName = "default.png";
        Path sourcePath = Paths.get("src/main/resources/default.png");
        Path targetPath = Paths.get(UPLOAD_DIR, defaultFileName);

        Files.createDirectories(targetPath.getParent()); // Create directories if not exist

        if (!Files.exists(targetPath)) {
            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
        } // Only copy if default image doesn't exist

        HouseImage defaultImage = new HouseImage();
        defaultImage.setFileName(defaultFileName);
        defaultImage.setHouse(house);
        house.getHouseImages().add(defaultImage);
    }
    private HouseDTO convertToDTO(House house) {
        HouseDTO dto = new HouseDTO();
        dto.setHouseName(house.getHouseName());
        dto.setAddress(house.getAddress());
        dto.setBedrooms(house.getBedrooms());
        dto.setBathrooms(house.getBathrooms());
        dto.setDescription(house.getDescription());
        dto.setPrice(house.getPrice());
        return dto;
    }
}
