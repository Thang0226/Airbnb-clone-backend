package com.codegym.service.house;

import com.codegym.model.House;
import com.codegym.service.IGenerateService;

import java.time.LocalDate;
import java.util.List;

public interface IHouseService extends IGenerateService<House> {

    List<House> findAll();

    List<House> searchHousesAsc(String address, LocalDate checkIn, LocalDate checkOut, Integer minBedrooms, Integer minBathrooms, Integer minPrice, Integer maxPrice);

    List<House> searchHousesDesc(String address, LocalDate checkIn, LocalDate checkOut, Integer minBedrooms, Integer minBathrooms, Integer minPrice, Integer maxPrice);

}
