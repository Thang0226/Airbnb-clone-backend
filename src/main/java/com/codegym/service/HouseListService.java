package com.codegym.service;


import com.codegym.model.HouseList;
import com.codegym.model.HouseListStatusEnum;
import com.codegym.repository.IHouseListRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HouseListService implements IHouseListService {
    private final IHouseListRepository houseListRepository;

    public HouseListService(IHouseListRepository houseListRepository) {

        this.houseListRepository = houseListRepository;
    }

    public List<HouseList> getHousesForRented(String status) {
        try {
            HouseListStatusEnum statusEnum = HouseListStatusEnum.valueOf(status.toUpperCase());
            return houseListRepository.findByStatus(statusEnum);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status value: " + status);
        }
    }


}


