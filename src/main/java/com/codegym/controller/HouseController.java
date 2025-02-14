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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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

    @PostMapping("/create")
    public ResponseEntity<?> createHouse(
           @ModelAttribute HouseDTO houseDTO) {
        try {
            // Validate house data
            if (houseDTO.getBedrooms() < 1 || houseDTO.getBedrooms() > 10) {
                return ResponseEntity.badRequest().body("Bedrooms must be between 1 and 10");
            }
            if (houseDTO.getBathrooms() < 1 || houseDTO.getBathrooms() > 3) {
                return ResponseEntity.badRequest().body("Bathrooms must be between 1 and 3");
            }
            if (houseDTO.getPrice() < 100000) {
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

            System.out.println("Create house entity");

            // Handle images
            if (houseDTO.getHouseImages() != null && !houseDTO.getHouseImages().isEmpty()) {
                System.out.println("There are images!");
                for (MultipartFile image : houseDTO.getHouseImages()) {
                    // Validate image
                    System.out.println("Let's validate image types");
                    String contentType = image.getContentType();
                    if (!contentType.equals("image/jpeg") && !contentType.equals("image/png")) {
                        continue;
                    }

                    System.out.println(contentType);


                    // Add to folder
                    String fileName = image.getOriginalFilename();
                    Path filePath = Paths.get(UPLOAD_DIR, fileName);
                    System.out.println(fileName);

                    // Create directories if they do not exist
                    Files.createDirectories(filePath.getParent());

                    // Save file to disk
                    Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);



                    System.out.println("Added to folder");
                    // Create and add HouseImage entity
                    HouseImage houseImage = new HouseImage();
                    houseImage.setFileName(fileName);
                    houseImage.setHouse(house);
                    house.getHouseImages().add(houseImage);
                }
            } else {
                // Add default image if no images provided
                String defaultFileName = "default.png";
                Path sourcePath = Paths.get("src/main/resources/default.png");
                Path targetPath = Paths.get(UPLOAD_DIR, defaultFileName);
                // Create directories if they do not exist
                Files.createDirectories(targetPath.getParent());
                Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);

                HouseImage defaultImage = new HouseImage();
                defaultImage.setFileName(defaultFileName);
                defaultImage.setHouse(house);
                house.getHouseImages().add(defaultImage);
            }

            System.out.println("Let's save images");

            // Save house with images
             houseService.save(house);
             return ResponseEntity.ok(house);
//            houseService.save(house);
//            Optional<House> savedHouse = houseService.findById(house.getId());
//
//            return savedHouse.map(value -> new ResponseEntity<>(value, HttpStatus.CREATED))
//                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create house: " + e.getMessage());
        }
    }
//    @PostMapping(path = "/create", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
//    public ResponseEntity<?> createHouse(
//            @RequestPart("house") House house,
//            @RequestPart("houseImages") MultipartFile file
//    ) {
//        try {
//            // Save file to server
//            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
//            Path filePath = Paths.get(UPLOAD_DIR + fileName);
//            Files.createDirectories(filePath.getParent());
//            Files.write(filePath, file.getBytes());
//
//            // Set file path in the database
//            house.setHouseImages(fileName);
//
//            houseService.save(house);
//            Optional<House> savedHouse = houseService.findById(house.getId());
//
//            return savedHouse.map(value -> new ResponseEntity<>(value, HttpStatus.CREATED))
//                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
//        } catch (IOException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
}
