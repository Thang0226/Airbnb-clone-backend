package com.codegym.service.house;

import com.codegym.model.House;
import com.codegym.service.IGenerateService;

import java.time.LocalDate;
import java.util.List;

public interface IHouseService extends IGenerateService<House> {
    List<House> getHousesForAVAILABLE(String status);

    List<House> findAll();

    List<House> searchHouses(String address, LocalDate checkIn, LocalDate checkOut, Integer guests, String sortOrder, Integer minBedrooms, Integer minBathrooms);

}
