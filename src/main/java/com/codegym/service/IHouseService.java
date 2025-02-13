package com.codegym.service;

import com.codegym.model.House;
import java.util.List;

public interface IHouseService {
    List<House> getHousesForAVAILABLE(String status);

    List<House> findAll();
}
