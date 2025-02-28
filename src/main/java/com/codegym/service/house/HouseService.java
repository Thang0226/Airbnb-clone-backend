package com.codegym.service.house;


import com.codegym.exception.HouseNotFoundException;
import com.codegym.model.Availability;
import com.codegym.model.House;
import com.codegym.model.HouseImage;
import com.codegym.model.User;
import com.codegym.model.dto.house.HouseDTO;
import com.codegym.model.constants.HouseStatus;
import com.codegym.model.dto.house.HouseListDTO;
import com.codegym.repository.IHouseImageRepository;
import com.codegym.repository.IHouseRepository;
import com.codegym.service.user.IUserService;
import com.codegym.service.availability.IAvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HouseService implements IHouseService {

    @Autowired
    private IHouseRepository houseRepository;

    @Autowired
    private IAvailabilityService availabilityService;

    @Autowired
    private IHouseImageRepository houseImageRepository;

    @Autowired
    private IUserService userService;

    @Override
    public List<House> findAll() {
        return houseRepository.findAll();
    }

    @Override
    public Optional<House> findById(Long id) {
        return houseRepository.findById(id);
    }

    @Override
    public void save(House house) {
        // if new house, initialize an availability time for house
        if (house.getId() == null) {
            Availability availability = new Availability();
            availability.setStartDate(LocalDate.now());
            availability.setEndDate(LocalDate.now().plusYears(3));
            availability.setHouse(house);
            availabilityService.save(availability);
        }
        houseRepository.save(house);
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

    @Override
    public Page<HouseListDTO> getHouseListByHostId(Long id, Pageable pageable) {
        int limit = pageable.getPageSize();
        int offset = (int) pageable.getOffset();

        List<HouseListDTO> houses = houseRepository.findHouseListByHostId(id, limit, offset);

        return new PageImpl<>(houses, pageable, houses.size());
    }

    @Override
    public Page<HouseListDTO> searchHostHouse(Long id, String houseName, String status, Pageable pageable) {
        int limit = pageable.getPageSize();
        int offset = (int) pageable.getOffset();

        List<HouseListDTO> houses = houseRepository.searchHostHouse(id, houseName, status, limit, offset);
        return new PageImpl<>(houses, pageable, houses.size());
    }


    @Override
    public List<HouseImage> findImagesByHouseId(Long houseId) {
        return houseImageRepository.findByHouseId(houseId);
    }
    // Update house details
    @Value("${FILE_UPLOAD}")
    private String UPLOAD_DIR;
    @Override
    public void updateHouse(Long houseId, HouseDTO houseDTO) throws IOException {
        House house = houseRepository.findById(houseId)
                .orElseThrow(() -> new RuntimeException("House not found"));

        // Validation
        if (houseDTO.getBedrooms() == null || houseDTO.getBedrooms() < 1 || houseDTO.getBedrooms() > 10) {
            throw new IllegalArgumentException("Bedrooms must be between 1 and 10");
        }
        if (houseDTO.getBathrooms() == null || houseDTO.getBathrooms() < 1 || houseDTO.getBathrooms() > 3) {
            throw new IllegalArgumentException("Bathrooms must be between 1 and 3");
        }
        if (houseDTO.getPrice() == null || houseDTO.getPrice() < 100000) {
            throw new IllegalArgumentException("Price must be at least 100,000 VND");
        }

        // Update data
        house.setHouseName(houseDTO.getHouseName() != null ? houseDTO.getHouseName() : house.getHouseName());
        house.setAddress(houseDTO.getAddress() != null ? houseDTO.getAddress() : house.getAddress());
        house.setBedrooms(houseDTO.getBedrooms());
        house.setBathrooms(houseDTO.getBathrooms());
        house.setDescription(houseDTO.getDescription() != null ? houseDTO.getDescription() : house.getDescription());
        house.setPrice(houseDTO.getPrice());

        // Validate user
        if (houseDTO.getUsername() != null) {
            Optional<User> userOptional = userService.findByUsername(houseDTO.getUsername());
            if (userOptional.isPresent()) {
                house.setHost(userOptional.get());
            } else {
                throw new IllegalArgumentException("Username not found");
            }
        }

        // Update images
        handleHouseImages(house, houseDTO);
        houseRepository.save(house);
    }
    private void handleHouseImages(House house, HouseDTO houseDTO) throws IOException {
        // Get the list of existing image IDs to keep
        List<Long> existingImageIds = houseDTO.getExistingImageIds();
        if (existingImageIds == null) {
            existingImageIds = new ArrayList<>();
        }

        // 1. Get all existing images
        List<HouseImage> allExistingImages = houseImageRepository.findByHouseId(house.getId());

        // 2. Create a list for updated images (existing images we wanna keep + new ones)
        List<HouseImage> updatedImages = new ArrayList<>();

        // 3. Add existing images that should be kept (filter by ID)
        for (HouseImage existingImage : allExistingImages) {
            if (existingImageIds.contains(existingImage.getId())) {
                updatedImages.add(existingImage);
            }
        }

        // 4. Add new uploaded images
        if (houseDTO.getHouseImages() != null && !houseDTO.getHouseImages().isEmpty()) {
            for (MultipartFile file : houseDTO.getHouseImages()) {
                // Skip invalid files or empty files
                if (file.isEmpty() || file.getContentType() == null ||
                        (!file.getContentType().equals("image/jpeg") && !file.getContentType().equals("image/png"))) {
                    continue;
                }

                // Add to folder
                String fileName = file.getOriginalFilename();
                Path filePath = Paths.get(UPLOAD_DIR, fileName);
                // Create directories if they do not exist
                Files.createDirectories(filePath.getParent());
                // Save file to disk
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                HouseImage newImage = new HouseImage();
                newImage.setFileName(fileName);
                newImage.setHouse(house);
                updatedImages.add(newImage);
            }
        }

        // 6. Update the house with the final list of images
        house.getHouseImages().clear();
        house.getHouseImages().addAll(updatedImages);
    }

    @Override
    public void updateHouseStatus(Long houseId, String status) {
        House house = houseRepository.findById(houseId)
                .orElseThrow(() -> new HouseNotFoundException("House with id " + houseId + " not found"));
        if (house.getStatus() == HouseStatus.RENTED) {
            throw new IllegalStateException("Cannot change status because the house is currently RENTED");
        }
        switch (status) {
            case "AVAILABLE":
                house.setStatus(HouseStatus.AVAILABLE);
                houseRepository.save(house);
                break;
            case "MAINTAINING":
                house.setStatus(HouseStatus.MAINTAINING);
                houseRepository.save(house);
                break;
            default:
                throw new IllegalArgumentException("Invalid status: " + status);
        }
    }
}
