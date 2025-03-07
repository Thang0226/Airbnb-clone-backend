package com.codegym.service.house;


import com.codegym.exception.HouseNotFoundException;
import com.codegym.mapper.HouseMapper;
import com.codegym.model.Availability;
import com.codegym.model.House;
import com.codegym.model.HouseImage;
import com.codegym.model.User;
import com.codegym.model.constants.HouseStatus;
import com.codegym.model.dto.house.NewHouseDTO;
import com.codegym.model.dto.house.HouseListDTO;
import com.codegym.model.dto.house.TopFiveHousesDTO;
import com.codegym.repository.IHouseRepository;
import com.codegym.service.availability.IAvailabilityService;
import com.codegym.service.houseImage.IHouseImageService;
import com.codegym.service.user.IUserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
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

@Service
public class HouseService implements IHouseService {

    @Autowired
    private IHouseRepository houseRepository;

    @Autowired
    private IAvailabilityService availabilityService;

    @Autowired
    private IHouseImageService houseImageService;

    @Autowired
    private IUserService userService;

    @Autowired
    private HouseMapper houseMapper;

    @Override
    public List<House> findAll() {
        return houseRepository.findAll();
    }

    @Override
    public Optional<House> findById(Long id) {
        return houseRepository.findById(id);
    }

    @Override
    @Transactional
    public void save(House house) {
        boolean isNewHouse = (house.getId() == null);

        if (isNewHouse) {
            house.setStatus(HouseStatus.AVAILABLE);
            house.setRentals(0);
        }
        houseRepository.save(house);
        // if new house, initialize an availability time for house
        if (isNewHouse) {
            Availability availability = new Availability();
            availability.setStartDate(LocalDate.now());
            availability.setEndDate(LocalDate.now().plusYears(3));
            availability.setHouse(house);
            availabilityService.save(availability);
        }
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
        return houseImageService.findAllByHouse_Id(houseId);
    }
    // Update house details
    @Value("${FILE_UPLOAD}")
    private String UPLOAD_DIR;
    @Override
    public void updateHouse(Long houseId, NewHouseDTO newHouseDTO) throws IOException {
        House house = houseRepository.findById(houseId)
                .orElseThrow(() -> new RuntimeException("House not found"));

        // Validation
        if (newHouseDTO.getBedrooms() == null || newHouseDTO.getBedrooms() < 1 || newHouseDTO.getBedrooms() > 10) {
            throw new IllegalArgumentException("Bedrooms must be between 1 and 10");
        }
        if (newHouseDTO.getBathrooms() == null || newHouseDTO.getBathrooms() < 1 || newHouseDTO.getBathrooms() > 3) {
            throw new IllegalArgumentException("Bathrooms must be between 1 and 3");
        }
        if (newHouseDTO.getPrice() == null || newHouseDTO.getPrice() < 100000) {
            throw new IllegalArgumentException("Price must be at least 100,000 VND");
        }

        // Update data
        house.setHouseName(newHouseDTO.getHouseName() != null ? newHouseDTO.getHouseName() : house.getHouseName());
        house.setAddress(newHouseDTO.getAddress() != null ? newHouseDTO.getAddress() : house.getAddress());
        house.setBedrooms(newHouseDTO.getBedrooms());
        house.setBathrooms(newHouseDTO.getBathrooms());
        house.setDescription(newHouseDTO.getDescription() != null ? newHouseDTO.getDescription() : house.getDescription());
        house.setPrice(newHouseDTO.getPrice());

        // Validate user
        if (newHouseDTO.getUsername() != null) {
            Optional<User> userOptional = userService.findByUsername(newHouseDTO.getUsername());
            if (userOptional.isPresent()) {
                house.setHost(userOptional.get());
            } else {
                throw new IllegalArgumentException("Username not found");
            }
        }

        // Update images
        handleHouseImages(house, newHouseDTO);
        houseRepository.save(house);
    }
    private void handleHouseImages(House house, NewHouseDTO newHouseDTO) throws IOException {
        // 1. Get the list of existing image IDs to keep
        List<Long> existingImageIds = newHouseDTO.getExistingImageIds();
        if (existingImageIds == null) {
            existingImageIds = new ArrayList<>();
        }
        List<HouseImage> allExistedImages = houseImageService.findAllByHouse_Id(house.getId());

        // 2. Delete existedImage that is not in new existing images
        for (HouseImage existedImage : allExistedImages) {
            if (!existingImageIds.contains(existedImage.getId())) {
                houseImageService.deleteById(existedImage.getId());
            }
        }

        // 3. Add new uploaded images
        if (newHouseDTO.getHouseImages() != null && !newHouseDTO.getHouseImages().isEmpty()) {
            for (MultipartFile file : newHouseDTO.getHouseImages()) {
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
                // Save new houseImage
                HouseImage newImage = new HouseImage();
                newImage.setFileName(fileName);
                newImage.setHouse(house);
                houseImageService.save(newImage);
            }
        }
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

    @Override
    public Page<TopFiveHousesDTO> getTopFiveRentalCountHouses(Pageable pageable) {
        Page<House> houses = houseRepository.findTop5ByOrderByRentalsDesc(pageable);
        return houses.map(houseMapper::toTopFiveHousesDTO);
    }
}
