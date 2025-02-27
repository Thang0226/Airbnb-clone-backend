package com.codegym.service.house;


import com.codegym.model.Availability;
import com.codegym.model.House;
import com.codegym.model.dto.house.HouseListDTO;
import com.codegym.repository.IHouseRepository;
import com.codegym.service.availability.IAvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class HouseService implements IHouseService {

    @Autowired
    private IHouseRepository houseRepository;

    @Autowired
    private IAvailabilityService availabilityService;

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
        houseRepository.save(house);
        Availability availability = new Availability();
        availability.setStartDate(LocalDate.now());
        availability.setEndDate(LocalDate.now().plusYears(10));
        availability.setHouse(house);
        availabilityService.save(availability);
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
}
