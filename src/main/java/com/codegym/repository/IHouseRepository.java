package com.codegym.repository;

import com.codegym.model.House;
import com.codegym.model.HouseStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IHouseRepository extends JpaRepository<House, Long> {

    List<House> findByStatus(HouseStatus status);
}
