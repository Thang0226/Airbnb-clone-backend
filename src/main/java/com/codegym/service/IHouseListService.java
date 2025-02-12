package com.codegym.service;

import com.codegym.model.HouseList;
import java.util.List;

public interface IHouseListService {
    List<HouseList> getHousesForRented(String status);
}
