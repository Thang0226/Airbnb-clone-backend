package com.codegym.repository;

import com.codegym.model.HouseImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IHouseImageRepository extends JpaRepository<HouseImage, Long> {

    List<HouseImage> findAllByHouse_Id(Long houseId);

    void deleteById(Long imageId);
}
