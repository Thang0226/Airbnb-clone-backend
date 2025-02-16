package com.codegym.repository;

import com.codegym.model.HouseList;
import com.codegym.model.HouseListStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IHouseListRepository extends JpaRepository<HouseList, Long> {

    List<HouseList> findByStatus(HouseListStatusEnum status);
}
