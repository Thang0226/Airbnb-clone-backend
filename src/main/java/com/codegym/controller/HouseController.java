package com.codegym.controller;

import com.codegym.model.House;
import com.codegym.model.HouseImage;
import com.codegym.model.dto.HouseDTO;
import com.codegym.service.house.IHouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/houses")
@CrossOrigin("*")
public class HouseController {

    @Autowired
    private IHouseService houseService;

    @GetMapping
    public ResponseEntity<Iterable<House>> getAllHouses(){
        return new ResponseEntity<>(houseService.findAll(), HttpStatus.OK);
    }

    @Value("${file_upload}")
    private String UPLOAD_DIR;

    @PostMapping(path ="/create", consumes = { "multipart/form-data" })
    public ResponseEntity<?> createHouse(@ModelAttribute HouseDTO houseDTO) {
        try {
            // Validate house data
            if (houseDTO.getBedrooms() == null || houseDTO.getBedrooms() < 1 || houseDTO.getBedrooms() > 10) {
                return ResponseEntity.badRequest().body("Bedrooms must be between 1 and 10");
            }
            if (houseDTO.getBathrooms() == null || houseDTO.getBathrooms() < 1 || houseDTO.getBathrooms() > 3) {
                return ResponseEntity.badRequest().body("Bathrooms must be between 1 and 3");
            }
            if (houseDTO.getPrice() == null || houseDTO.getPrice() < 100000) {
                return ResponseEntity.badRequest().body("Price must be at least 100,000 VND");
            }

            // Create house entity
            House house = new House();
            house.setHouseName(houseDTO.getHouseName());
            house.setAddress(houseDTO.getAddress());
            house.setBedrooms(houseDTO.getBedrooms());
            house.setBathrooms(houseDTO.getBathrooms());
            house.setDescription(houseDTO.getDescription());
            house.setPrice(houseDTO.getPrice());

            // Initialize house images list
            if (house.getHouseImages() == null) {
                house.setHouseImages(new ArrayList<>());
            }

            // Log the contents of houseImages
            List<MultipartFile> houseImages = houseDTO.getHouseImages();
            System.out.println("Received houseImages: " + (houseImages != null ? houseImages.size() : "null"));

            // Handle images
            if (houseImages != null && !houseImages.isEmpty()) {
                for (MultipartFile image : houseImages) {
                    // Validate image
                    String contentType = image.getContentType();
                    if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
                        continue;
                    }

                    // Add to folder
                    String fileName = image.getOriginalFilename();
                    Path filePath = Paths.get(UPLOAD_DIR, fileName);

                    // Create directories if they do not exist
                    Files.createDirectories(filePath.getParent());

                    // Save file to disk
                    Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                    // Create and add HouseImage entity
                    HouseImage houseImage = new HouseImage();
                    houseImage.setFileName(fileName);
                    houseImage.setHouse(house);
                    house.getHouseImages().add(houseImage);
                }
                // If no valid images were added (all were invalid types), add default image
                if (house.getHouseImages().isEmpty()) {
                    addDefaultImage(house);
                }
            } else {
                addDefaultImage(house);
            }

            // Save house with images
            houseService.save(house);
            return ResponseEntity.ok(house);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create house: " + e.getMessage());
        }
    }

    private void addDefaultImage(House house) throws IOException {
        String defaultFileName = "default.png";
        Path sourcePath = Paths.get("src/main/resources/default.png");
        Path targetPath = Paths.get(UPLOAD_DIR, defaultFileName);

        // Create directories if they do not exist
        Files.createDirectories(targetPath.getParent());

        // Only copy if default image doesn't exist in upload directory
        if (!Files.exists(targetPath)) {
            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }

        HouseImage defaultImage = new HouseImage();
        defaultImage.setFileName(defaultFileName);
        defaultImage.setHouse(house);
        house.getHouseImages().add(defaultImage);
    }
}
