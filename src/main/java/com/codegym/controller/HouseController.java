package com.codegym.controller;

import com.codegym.mapper.BookingDTOMapper;
import com.codegym.mapper.HouseMaintenanceMapper;
import com.codegym.mapper.HouseMapper;
import com.codegym.mapper.ReviewDTOMapper;
import com.codegym.model.*;
import com.codegym.model.auth.Role;
import com.codegym.model.dto.booking.NewBookingDTO;
import com.codegym.model.dto.host.HostChatDTO;
import com.codegym.model.dto.house.*;
import com.codegym.model.dto.SearchDTO;
import com.codegym.model.dto.review.ReviewDTO;
import com.codegym.repository.IHouseImageRepository;
import com.codegym.service.availability.IAvailabilityService;
import com.codegym.service.booking.IBookingService;
import com.codegym.service.house.IHouseMaintenanceService;
import com.codegym.service.houseImage.HouseImageService;
import com.codegym.service.review.IReviewService;
import com.codegym.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    private IUserService userService;

    @Autowired
    private IHouseImageRepository houseImageRepository;
    @Autowired
    private IHouseService houseService;
    @Autowired
    private HouseMapper houseMapper;
    @Autowired
    private HouseImageService houseImageService;

    @Autowired
    private IHouseMaintenanceService houseMaintenanceService;
    @Autowired
    private HouseMaintenanceMapper houseMaintenanceMapper;

    @Autowired
    private IBookingService bookingService;
    @Autowired
    private BookingDTOMapper bookingDTOMapper;
    @Autowired
    private IAvailabilityService availabilityService;

    @Autowired
    private NotificationController notificationController;
    @Autowired
    private IReviewService reviewService;
    @Autowired
    private ReviewDTOMapper reviewDTOMapper;

    @GetMapping
    public ResponseEntity<?> getHousesForAvailable() {
        List<House> houses;
        houses = houseService.searchHousesDesc(null, null, null, null, null, null, null);
        List<HouseDTO> houseDTOs = houses.stream().map(house -> houseMapper.toHouseDTO(house)).toList();
        return ResponseEntity.ok(houseDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getHouseById(@PathVariable Long id){
        Optional<House> houseOptional = houseService.findById(id);
        if (houseOptional.isPresent()) {
            House house = houseOptional.get();
            return ResponseEntity.ok(houseMapper.toHouseDTO(house));
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{houseId}/host")
    public ResponseEntity<HostChatDTO> getHouseHost(@PathVariable Long houseId) {
        Optional<House> houseOptional = houseService.findById(houseId);
        if (houseOptional.isEmpty()) {
            System.out.println("House not found" );
            return ResponseEntity.notFound().build();
        }
        House house = houseOptional.get();
        User host = house.getHost();

        String role = null;
        for (Role userRole : host.getRoles()) {
            if (userRole.getName().equals("ROLE_HOST")) {
                role = userRole.getName();
                break;
            }
        }
        if (role == null && !host.getRoles().isEmpty()) {
            role = host.getRoles().iterator().next().getName();
        }
        HostChatDTO hostChatDTO = new HostChatDTO(
                host.getId(),
                host.getUsername(),
                role
        );
        return ResponseEntity.ok(hostChatDTO);
    }

    @GetMapping("/host/{hostId}")
    public ResponseEntity<?> getAllHouseByHostId(@PathVariable Long hostId) {
        List<House> houses = houseService.findHousesByHostId(hostId);
        List<HouseDTO> houseDTOs = houses.stream().map(h -> houseMapper.toHouseDTO(h)).toList();
        return new ResponseEntity<>(houseDTOs, HttpStatus.OK);
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
    public ResponseEntity<?> searchHouses(@RequestBody SearchDTO searchDTO) {
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
        List<HouseDTO> houseDTOS = houses.stream().map(h -> houseMapper.toHouseDTO(h)).toList();
        return ResponseEntity.ok(houseDTOS);
    }

    // Create House
    @Value("${FILE_UPLOAD}")
    private String UPLOAD_DIR;

    @PostMapping(path ="/create", consumes = { "multipart/form-data" })
    public ResponseEntity<?> createHouse(@ModelAttribute NewHouseDTO newHouseDTO) {
        try {
            // Validate house data
            if (newHouseDTO.getBedrooms() == null || newHouseDTO.getBedrooms() < 1 || newHouseDTO.getBedrooms() > 10) {
                return ResponseEntity.badRequest().body("Bedrooms must be between 1 and 10");
            }
            if (newHouseDTO.getBathrooms() == null || newHouseDTO.getBathrooms() < 1 || newHouseDTO.getBathrooms() > 3) {
                return ResponseEntity.badRequest().body("Bathrooms must be between 1 and 3");
            }
            if (newHouseDTO.getPrice() == null || newHouseDTO.getPrice() < 100000) {
                return ResponseEntity.badRequest().body("Price must be at least 100,000 VND");
            }
            // Create house entity
            House house = new House();
            house.setHouseName(newHouseDTO.getHouseName());
            house.setAddress(newHouseDTO.getAddress());
            house.setBedrooms(newHouseDTO.getBedrooms());
            house.setBathrooms(newHouseDTO.getBathrooms());
            house.setDescription(newHouseDTO.getDescription());
            house.setPrice(newHouseDTO.getPrice());
            Optional<User> userOptional = userService.findByUsername(newHouseDTO.getUsername());
            if (userOptional.isPresent()) {
                house.setHost(userOptional.get());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username not found");
            }
            houseService.save(house);

            // Save house images
            List<MultipartFile> houseImages = newHouseDTO.getHouseImages();
            System.out.println("Received houseImages: " + (houseImages != null ? houseImages.size() : "null"));
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
                    houseImageService.save(houseImage);
                }
            } else {
                addDefaultImage(house);
            }
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
        houseImageService.save(defaultImage);
    }


    // Update House + Upload images:
    @PutMapping("/update/{houseId}")
    public ResponseEntity<?> updateHouse(@PathVariable Long houseId, @ModelAttribute NewHouseDTO newHouseDTO) {
        try {
            houseService.updateHouse(houseId, newHouseDTO);
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
        if (hidden == 1) {
            List<Review> filteredReviews = new ArrayList<>();
            for (Review review : reviews) {
                if (review.isHidden()) {
                    continue;
                }
                filteredReviews.add(review);
            }
            reviews = filteredReviews;
        }
        List<ReviewDTO> reviewDTOS = reviews.stream().map(reviewDTOMapper::toReviewDTO).toList();
        return ResponseEntity.ok(reviewDTOS);
    }
}
