package com.codegym.service;

import com.codegym.model.HouseList;
import java.util.List;

public interface IHouseListService {
    List<HouseList> getHousesForAVAILABLE(String status);
}
