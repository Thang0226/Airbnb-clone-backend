package com.codegym.service;


import com.codegym.model.House;
import com.codegym.model.HouseStatus;
import com.codegym.repository.IHouseRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HouseService implements IHouseService {

    private final IHouseRepository houseRepository;

    public HouseService(IHouseRepository houseRepository) {

        this.houseRepository = houseRepository;
    }

    public List<House> getHousesForAVAILABLE(String status) {
        try {
            HouseStatus statusEnum = HouseStatus.valueOf(status.toUpperCase());
            return houseRepository.findByStatus(statusEnum);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status value: " + status);
        }
    }

    @Override
    public List<House> findAll() {
        return houseRepository.findAll();
    }
//    public List<House> searchHouses(Specification<House> spec, Sort sort);

}


