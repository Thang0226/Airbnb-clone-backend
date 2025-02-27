package com.codegym.service.house;


import com.codegym.model.House;
import com.codegym.model.HouseImage;
import com.codegym.model.User;
import com.codegym.model.dto.house.HouseDTO;
import com.codegym.model.dto.house.HouseListDTO;
import com.codegym.repository.IHouseImageRepository;
import com.codegym.repository.IHouseRepository;
import com.codegym.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.nio.file.Paths;
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

// Upload image: find image by house id, save, delete

    @Override
    public List<HouseImage> findImagesByHouseId(Long houseId) {
        return houseImageRepository.findByHouseId(houseId);
    }

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

        // Handle image updates
        handleHouseImages(house, houseDTO);
//        // Merge new + old images
//        List<HouseImage> existingImages = houseImageRepository.findByHouseId(houseId);
//        List<Long> existingImageIds = houseDTO.getExistingImageIds();
//
//        // Remove images that were NOT included in the request
//        List<HouseImage> updatedImages = existingImages.stream()
//                .filter(img -> existingImageIds.contains(img.getId()))
//                .toList();
//
//        // Add new images
//        if (houseDTO.getHouseImages() != null && !houseDTO.getHouseImages().isEmpty()) {
//            for (MultipartFile file : houseDTO.getHouseImages()) {
//                if (file.getContentType() == null || (!file.getContentType().equals("image/jpeg") && !file.getContentType().equals("image/png"))) {
//                    continue; // Skip invalid image types
//                }
//
//                HouseImage newImage = new HouseImage();
//                newImage.setFileName(file.getOriginalFilename());
//                newImage.setData(file.getBytes());
//                newImage.setHouse(house);
//                updatedImages.add(newImage);
//            }
//        }
//
//        // If no images remain, add a default image
//        if (updatedImages.isEmpty()) {
//            HouseImage defaultImage = new HouseImage();
//            defaultImage.setFileName("default.png");
//            defaultImage.setData(loadDefaultImage());
//            defaultImage.setHouse(house);
//            updatedImages.add(defaultImage);
//        }
//
//        house.setHouseImages(updatedImages);
        // Save all updates
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

                HouseImage newImage = new HouseImage();
                newImage.setFileName(file.getOriginalFilename());
//                newImage.setData(file.getBytes());
                newImage.setHouse(house);
                updatedImages.add(newImage);
            }
        }

        // 5. Check if we have any images (from updatedImages)
        // If not, add a default image
        // Only add default image if no images remain AND no new images were uploaded
//        if (updatedImages.isEmpty()) {
//            // Look for existing default image
//            HouseImage defaultImage = allExistingImages.stream()
//                    .filter(img -> "default.png".equals(img.getFileName()))
//                    .findFirst()
//                    .orElse(null);
//
//            if (defaultImage == null) {
//                // Create new default image
//                defaultImage = new HouseImage();
//                defaultImage.setFileName("default.png");
//                defaultImage.setData(loadDefaultImage());
//                defaultImage.setHouse(house);
//            }
//
//            updatedImages.add(defaultImage);
//        } else {
//            // If we have actual images, make sure we remove any default image
//            updatedImages = updatedImages.stream()
//                    .filter(img -> !"default.png".equals(img.getFileName()))
//                    .collect(Collectors.toList());
//        }

        // 6. Update the house with the final list of images
        house.getHouseImages().clear();
        house.getHouseImages().addAll(updatedImages);
    }

    @Override
    public void saveHouseImages(Long houseId, List<MultipartFile> files, List<Long> existingImageIds) throws IOException {
        House house = houseRepository.findById(houseId)
                .orElseThrow(() -> new RuntimeException("House not found"));

        // Keep images user didn't delete
        List<HouseImage> existingImages = houseImageRepository.findByHouseId(houseId)
                .stream()
                .filter(image -> existingImageIds.contains(image.getId())) // Only keep images user didn't delete
                .toList();

        // Add new images
        for (MultipartFile file : files) {
            HouseImage image = new HouseImage();
            image.setFileName(file.getOriginalFilename());
//            image.setData(file.getBytes());
            image.setHouse(house);
            existingImages.add(image);
        }

        // Save updated image list
        house.setHouseImages(existingImages);
        houseRepository.save(house);
    }

    @Override
    public void deleteHouseImage(Long imageId, Long houseId) {
        houseImageRepository.deleteById(imageId);

//        if (houseImageRepository.findByHouseId(houseId).isEmpty()) {
//            HouseImage defaultImage = new HouseImage();
//            defaultImage.setFileName("default.png");
//            defaultImage.setData(loadDefaultImage());
//            defaultImage.setHouse(houseRepository.findById(houseId).orElseThrow());
//            houseImageRepository.save(defaultImage);
//        }
    }

//    private byte[] loadDefaultImage() {
//        // Load default image from resources
//        try {
//            Resource resource = new ClassPathResource("src/main/resources/default.png");
//            return FileCopyUtils.copyToByteArray(resource.getInputStream());
////            return Files.readAllBytes(Paths.get(""));
//        } catch (IOException e) {
//            throw new RuntimeException("Default image not found.");
//        }
//    }
}
