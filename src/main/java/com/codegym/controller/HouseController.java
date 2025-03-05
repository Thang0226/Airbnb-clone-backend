package com.codegym.controller;

import com.codegym.mapper.BookingDTOMapper;
import com.codegym.mapper.HouseMaintenanceMapper;
import com.codegym.mapper.ReviewDTOMapper;
import com.codegym.model.*;
import com.codegym.model.dto.booking.NewBookingDTO;
import com.codegym.model.dto.house.HouseDateDTO;
import com.codegym.model.dto.SearchDTO;
import com.codegym.model.dto.house.HouseListDTO;
import com.codegym.model.dto.review.ReviewDTO;
import com.codegym.repository.IHouseImageRepository;
import com.codegym.model.dto.house.HouseMaintenanceRecordDTO;
import com.codegym.service.availability.IAvailabilityService;
import com.codegym.service.booking.IBookingService;
import com.codegym.service.house.IHouseMaintenanceService;
import com.codegym.service.review.IReviewService;
import com.codegym.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.codegym.model.dto.house.HouseDTO;
import com.codegym.service.house.IHouseService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/houses")
public class HouseController {

    @Autowired
    private IHouseImageRepository houseImageRepository;
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

    @Autowired
    private IHouseMaintenanceService houseMaintenanceService;
    @Autowired
    private HouseMaintenanceMapper houseMaintenanceMapper;

    @Autowired
    private NotificationController notificationController;
    @Autowired
    private IReviewService reviewService;
    @Autowired
    private ReviewDTOMapper reviewDTOMapper;

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
            if (houseOptional.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("House not found");
            House house = houseOptional.get();

            // Notify host
            User host = house.getHost();
            String message = '"'+booking.getUser().getUsername()+'"'+" BOOKED the house "+'"'+booking.getHouse().getHouseName()+'"'
                    + " on " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            notificationController.sendNotification(host, message);

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

    // Create House
    @Value("${FILE_UPLOAD}")
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


    // Update House + Upload images:
    @PutMapping("/update/{houseId}")
    public ResponseEntity<?> updateHouse(@PathVariable Long houseId, @ModelAttribute HouseDTO houseDTO) {
        try {
            houseService.updateHouse(houseId, houseDTO);
            // Return the updated house
            Optional<House> updatedHouse = houseService.findById(houseId);
            if (updatedHouse.isPresent()) {
                return ResponseEntity.ok(updatedHouse.get());
            }
            return ResponseEntity.ok("House updated successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update house: " + e.getMessage());
        }
    }
    @GetMapping("/{houseId}/images")
    public ResponseEntity<List<HouseImage>> getHouseImages(@PathVariable Long houseId) {
        return ResponseEntity.ok(houseService.findImagesByHouseId(houseId));
    }
    @DeleteMapping("/images/{imageId}")
    public ResponseEntity<?> deleteImage(@PathVariable Long imageId, @RequestParam Long houseId) {
        try {
            // Validate that the image belongs to the house
            List<HouseImage> houseImages = houseService.findImagesByHouseId(houseId);
            boolean imageExists = houseImages.stream().anyMatch(img -> img.getId().equals(imageId));
            if (!imageExists) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Image not found or doesn't belong to the house");
            }

            // Verify that we're not trying to delete the last image
            if (houseImages.size() <= 1) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot delete the last image");
            }

            // Delete image
            houseImageRepository.deleteById(imageId);
            return ResponseEntity.ok("Image deleted.");
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete image: " + e.getMessage());
        }
    }




    @GetMapping("/host-house-list/{username}")
    public ResponseEntity<?> getHostsHouses(@PathVariable String username, Pageable pageable) {
        Optional<User> userOptional = userService.findByUsername(username);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Username not found");
        }
        Page<HouseListDTO> houses = houseService.getHouseListByHostId(userOptional.get().getId(), pageable);
        return ResponseEntity.ok(houses);
    }

    @PostMapping("/host-house-list/{username}/search")
    public ResponseEntity<?> searchHostsHouses(
            @PathVariable String username,
            @RequestParam(required = false, defaultValue = "") String houseName,
            @RequestParam(required = false, defaultValue = "") String status,
            Pageable pageable) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Username not found"));
        Page<HouseListDTO> houses = houseService.searchHostHouse(
                user.getId(),
                houseName,
                status,
                pageable);
        return ResponseEntity.ok(houses);
    }

    @PostMapping("/create-maintenance-record")
    public ResponseEntity<?> createMaintenanceRecord(@RequestBody HouseMaintenanceRecordDTO dto) {
        HouseMaintenance houseMaintenance = houseMaintenanceMapper.toHouseMaintenance(dto, houseService);
        houseMaintenanceService.save(houseMaintenance);
        HouseMaintenanceRecordDTO responseDTO = houseMaintenanceMapper.toHouseMaintenanceRecordDTO(houseMaintenance);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping("/{houseId}/maintenance-records")
    public ResponseEntity<?> getMaintenanceRecords(@PathVariable Long houseId) {
        return ResponseEntity.ok(houseMaintenanceService.findByHouseId(houseId));
    }

    @PutMapping("/{houseId}/update-status")
    public ResponseEntity<?> updateStatus(@PathVariable Long houseId, @RequestParam String status) {
        try {
            houseService.updateHouseStatus(houseId, status);
            return ResponseEntity.ok("House status updated");
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    @GetMapping("/top-five-houses")
    public ResponseEntity<?> getTopFiveHouses(Pageable pageable) {
        return ResponseEntity.ok(houseService.getTopFiveRentalCountHouses(pageable));
    }

    @GetMapping("/{houseId}/reviews")
    public ResponseEntity<?> getHouseReviews(@PathVariable Long houseId, @RequestParam Integer hidden) {
        Optional<House> houseOptional = houseService.findById(houseId);
        if (houseOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("House not found");
        }
        List<Review> reviews = reviewService.findAllByHouseId(houseId);
        List<ReviewDTO> reviewDTOS = reviews.stream().map(reviewDTOMapper::toReviewDTO).toList();
        return ResponseEntity.ok(reviewDTOS);
    }
}
