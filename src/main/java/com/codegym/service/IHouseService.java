package com.codegym.service;

import com.codegym.model.House;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;

public interface IHouseService {
    List<House> getHousesForAVAILABLE(String status);

    List<House> findAll();

    List<House> searchHouses(LocalDate checkIn, LocalDate checkOut, Integer guests, String sortOrder, Integer minBedrooms, Integer minBathrooms);
}
