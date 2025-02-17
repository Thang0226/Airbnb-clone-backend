package com.codegym.repository;

import com.codegym.model.House;
import org.springframework.stereotype.Repository;
import com.codegym.model.HouseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

@Repository
public interface IHouseRepository extends JpaRepository<House, Long>, JpaSpecificationExecutor<House> {
  //cần JpaSpecificationExecutor để lọc và tìm kiếm
    List<House> findByStatus(HouseStatus status);
}
