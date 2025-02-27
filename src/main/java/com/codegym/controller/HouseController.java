package com.codegym.controller;

import com.codegym.mapper.BookingDTOMapper;
import com.codegym.model.*;
import com.codegym.model.constants.HouseStatus;
import com.codegym.model.dto.NewBookingDTO;
import com.codegym.model.dto.HouseDateDTO;
import com.codegym.model.dto.SearchDTO;
import com.codegym.service.availability.IAvailabilityService;
import com.codegym.service.booking.IBookingService;
import com.codegym.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.codegym.model.dto.HouseDTO;
import com.codegym.service.house.IHouseService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.*;


@RestController
@CrossOrigin("*")
@RequestMapping("/api/houses")
public class HouseController {

    @Autowired
    private IHouseService houseService;
    @Autowired
    private IUserService userService;

    @Autowired
    private IBookingService bookingService;
    @Autowired
    private BookingDTOMapper bookingDTOMapper;
    @Autowired
    private IAvailabilityService availabilityService;

    @GetMapping
    public ResponseEntity<List<House>> getHousesForAvailable() {
        List<House> houses;
        houses = houseService.searchHousesDesc(null, LocalDate.now(), LocalDate.now().plusDays(1), null, null, null, null);
        return ResponseEntity.ok(houses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<House> getHouseById(@PathVariable Long id){
        Optional<House> house = houseService.findById(id);
        return house.map(value ->
                        new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


    @GetMapping("/host/{hostId}")
    public ResponseEntity<?> getAllHouseByHostId(@PathVariable Long hostId) {
        List<House> houses = houseService.findHousesByHostId(hostId);
        if (houses.isEmpty()) {
            return new ResponseEntity<>("No houses found for this host", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(houses, HttpStatus.OK);
    }

    @GetMapping("/{id}/booked-dates")
    public ResponseEntity<List<NewBookingDTO>> getBookedDates(@PathVariable Long id){
        List<Booking> bookings = bookingService.getBookingsByHouseId(id);
        List<NewBookingDTO> newBookingDTOS = bookings.stream().map(booking -> bookingDTOMapper.toNewBookingDTO(booking)).toList();
        return ResponseEntity.ok(newBookingDTOS);
    }

    @GetMapping("/{id}/house-soonest-date")
    public ResponseEntity<?> findSoonestDate(@PathVariable Long id){
        Optional<House> houseOptional = houseService.findById(id);
        if (houseOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("House not found");
        }
        LocalDate soonestAvailableDate = availabilityService.findSoonestAvailableDate(houseOptional.get());
        return ResponseEntity.ok(soonestAvailableDate);
    }

    @PostMapping("/house-edge-date")
    public ResponseEntity<?> getNearestAvailableDateOfHouse(@RequestBody HouseDateDTO houseDateDTO){
        Optional<House> houseOptional = houseService.findById(houseDateDTO.getHouseId());
        if (houseOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("House not found");
        }
        LocalDate nearestAvailableDate = availabilityService.findNearestAvailableDate(houseOptional.get(), houseDateDTO.getDate());
        return ResponseEntity.ok(nearestAvailableDate);
    }

    @PostMapping("/rent-house")
    public ResponseEntity<?> rentHouse(@RequestBody NewBookingDTO newBookingDTO) {
        Booking booking = bookingDTOMapper.toBooking(newBookingDTO);
        try {
            bookingService.save(booking);
            Optional<House> houseOptional = houseService.findById(newBookingDTO.getHouseId());
            if (houseOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("House not found");
            } else {
                House house = houseOptional.get();
                house.setStatus(HouseStatus.RENTED);
                houseService.save(house);
            }
            return ResponseEntity.ok("Rent house successfully");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create house booking: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<List<House>> searchHouses(@RequestBody SearchDTO searchDTO) {
        List<House> houses;

        if (searchDTO.getPriceOrder().equals("ASC")) {
            houses = houseService.searchHousesAsc(
                    searchDTO.getAddress(),
                    searchDTO.getCheckIn(),
                    searchDTO.getCheckOut(),
                    searchDTO.getMinBedrooms(),
                    searchDTO.getMinBathrooms(),
                    searchDTO.getMinPrice(),
                    searchDTO.getMaxPrice()
            );
        } else {
            houses = houseService.searchHousesDesc(
                    searchDTO.getAddress(),
                    searchDTO.getCheckIn(),
                    searchDTO.getCheckOut(),
                    searchDTO.getMinBedrooms(),
                    searchDTO.getMinBathrooms(),
                    searchDTO.getMinPrice(),
                    searchDTO.getMaxPrice()
            );
        }
        return ResponseEntity.ok(houses);
    }

    @Value("${FILE_UPLOAD}")
    private String UPLOAD_DIR;

    @PostMapping(path ="/create", consumes = { "multipart/form-data" })
    public ResponseEntity<?> createHouse(@ModelAttribute HouseDTO houseDTO) {
        try {
            ResponseEntity<String> badRequestBody = validateInput(houseDTO);
            if (badRequestBody != null) return badRequestBody;

            // Create house entity
            House house = new House();
            house.setHouseName(houseDTO.getHouseName());
            house.setAddress(houseDTO.getAddress());
            house.setBedrooms(houseDTO.getBedrooms());
            house.setBathrooms(houseDTO.getBathrooms());
            house.setDescription(houseDTO.getDescription());
            house.setPrice(houseDTO.getPrice());

            Optional<User> userOptional = userService.findByUsername(houseDTO.getUsername());
            if (userOptional.isPresent()) {
                house.setHost(userOptional.get());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username not found");
            }

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



    @PutMapping(path = "/update/{id}", consumes = { "multipart/form-data" })
    public ResponseEntity<?> updateHouse(@PathVariable Long id,
                                         @RequestParam(value = "houseName", required = false) String houseName,
                                         @RequestParam(value = "address", required = false) String address,
                                         @RequestParam(value = "bedrooms", required = false) Integer bedrooms,
                                         @RequestParam(value = "bathrooms", required = false) Integer bathrooms,
                                         @RequestParam(value = "description", required = false) String description,
                                         @RequestParam(value = "price", required = false) Integer price,
                                         @RequestParam(value = "username", required = false) String username,
                                         @RequestParam(value = "houseImages", required = false) List<MultipartFile> houseImages,
                                         @RequestParam(value = "existingFiles", required = false) List<String> existingFiles) {
        try {
            HouseDTO houseDTO = new HouseDTO();
            houseDTO.setHouseName(houseName);
            houseDTO.setAddress(address);
            houseDTO.setBedrooms(bedrooms);
            houseDTO.setBathrooms(bathrooms);
            houseDTO.setDescription(description);
            houseDTO.setPrice(price);
            houseDTO.setUsername(username);
            houseDTO.setHouseImages(houseImages);
            houseDTO.setExistingFiles(existingFiles);

            ResponseEntity<String> validation = houseService.validateInput(houseDTO);
            if (validation != null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            Optional<House> optionalHouse = houseService.findById(id);
            if (optionalHouse.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            HouseDTO updatedHouse = houseService.updateHouseDetails(id, houseDTO);
            return new ResponseEntity<>(updatedHouse, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        /*
        try {
            Optional<House> selectedHouse = houseService.findById(houseId);
            if(selectedHouse.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("House not found");
            }
            House house = selectedHouse.get();

            // Validate
            ResponseEntity<String> badRequestBody = validateInput(houseDTO);
            if (badRequestBody != null) return badRequestBody;

            // If new details are null, keep the existing value
            house.setHouseName(houseDTO.getHouseName() != null ? houseDTO.getHouseName() : house.getHouseName());
            house.setAddress(houseDTO.getAddress() != null ? houseDTO.getAddress() : house.getAddress());
            house.setBedrooms(houseDTO.getBedrooms() != null ? houseDTO.getBedrooms() : house.getBedrooms());
            house.setBathrooms(houseDTO.getBathrooms() != null ? houseDTO.getBathrooms() : house.getBathrooms());
            house.setDescription(houseDTO.getDescription() != null ? houseDTO.getDescription() : house.getDescription());
            house.setPrice(houseDTO.getPrice() != null ? houseDTO.getPrice() : house.getPrice());

            // Find if username has role Host
            if (houseDTO.getUsername() != null) {
                Optional<User> userOptional = userService.findByUsername(houseDTO.getUsername());
                if (userOptional.isPresent()) {
                    house.setHost(userOptional.get());
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username not found");
                }
            }

            // Handle Images Upload
            List<MultipartFile> newHouseImages = houseDTO.getHouseImages();
            List<String> existingFiles = houseDTO.getExistingFiles();

            if (newHouseImages != null && !newHouseImages.isEmpty()) {
                // Clear all existing images, including default.png
//                for (HouseImage image : house.getHouseImages()) {
//                    Path imagePath = Paths.get(UPLOAD_DIR, image.getFileName());
//                    Files.deleteIfExists(imagePath);
//                }
//                house.getHouseImages().clear();

                for (MultipartFile image : newHouseImages) {
                    String contentType = image.getContentType();
                    if (contentType != null && (contentType.equals("image/jpeg") || contentType.equals("image/png"))) {
                        String fileName = image.getOriginalFilename();
                        Path filePath = Paths.get(UPLOAD_DIR, fileName);
                        Files.createDirectories(filePath.getParent());
                        Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                        HouseImage houseImage = new HouseImage();
                        houseImage.setFileName(fileName);
                        houseImage.setHouse(house);
                        house.getHouseImages().add(houseImage);
                    }
                }
            }
            else if (existingFiles != null && !existingFiles.isEmpty()) {
                Set<String> filesToKeep = new HashSet<>(existingFiles);
                List<HouseImage> imagesToRemove = new ArrayList<>();
                for (HouseImage image : house.getHouseImages()) {
                    if (!filesToKeep.contains(image.getFileName()) && !image.getFileName().equals("default.png")) {
                        imagesToRemove.add(image);
                        Path imagePath = Paths.get(UPLOAD_DIR, image.getFileName());
                        Files.deleteIfExists(imagePath);
                    }
                }
                house.getHouseImages().removeAll(imagesToRemove);
            }
            else {
                List<HouseImage> imagesToRemove = new ArrayList<>();
                for (HouseImage image : house.getHouseImages()) {
                    if (!image.getFileName().equals("default.png")) {
                        imagesToRemove.add(image);
                        Path imagePath = Paths.get(UPLOAD_DIR, image.getFileName());
                        Files.deleteIfExists(imagePath);
                    }
                }
                house.getHouseImages().removeAll(imagesToRemove);
                if (house.getHouseImages().isEmpty()) {
                    addDefaultImage(house);
                }
            }
            // Save updated house
            houseService.save(house);
            return ResponseEntity.ok(house);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update house: " + e.getMessage());
        }

        */
    }
    // Remove image from clicking X icon
    @DeleteMapping("/update/{id}/images/{imageId}")
    public ResponseEntity<?> removeImage(@PathVariable Long id, @PathVariable Long imageId) {
        try {
            Optional<House> optionalHouse = houseService.findById(id);
            if (optionalHouse.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            houseService.removeHouseImage(id, imageId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // Add new images (triggered by file upload)
    @PostMapping("/{id}/images")
    public ResponseEntity<Void> addImages(@PathVariable Long id, @RequestParam("images") List<MultipartFile> images) {
        try {
            Optional<House> optionalHouse = houseService.findById(id);
            if (optionalHouse.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            houseService.addHouseImages(id, images);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }










    private static ResponseEntity<String> validateInput(HouseDTO houseDTO) {
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
        return null;
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
