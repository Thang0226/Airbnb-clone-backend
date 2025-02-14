package com.codegym.service;

import com.codegym.model.House;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface IHouseService {
    List<House> getHousesForAVAILABLE(String status);

    List<House> findAll();

    List<House> findAlltoSearch(Specification<House> spec, Sort sort);
}
