package com.codegym.service.house;


import com.codegym.model.House;
import com.codegym.repository.IHouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class HouseService implements IHouseService {

    @Autowired
    private IHouseRepository houseRepository;

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
}


