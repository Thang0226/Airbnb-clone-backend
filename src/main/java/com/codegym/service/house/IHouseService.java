package com.codegym.service.house;

import com.codegym.model.House;
import com.codegym.model.HouseImage;
import com.codegym.model.dto.house.HouseDTO;
import com.codegym.model.dto.house.HouseListDTO;
import com.codegym.service.IGenerateService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface IHouseService extends IGenerateService<House> {

    List<House> findAll();

    List<House> searchHousesAsc(String address, LocalDate checkIn, LocalDate checkOut, Integer minBedrooms, Integer minBathrooms, Integer minPrice, Integer maxPrice);

    List<House> searchHousesDesc(String address, LocalDate checkIn, LocalDate checkOut, Integer minBedrooms, Integer minBathrooms, Integer minPrice, Integer maxPrice);

    List<House> findHousesByHostId(Long id);

    Page<HouseListDTO> getHouseListByHostId(Long id, Pageable pageable);

    Page<HouseListDTO> searchHostHouse(Long id, String houseName, String status, Pageable pageable);


    List<HouseImage> findImagesByHouseId(Long houseId);
    void updateHouse(Long houseId, HouseDTO houseDTO) throws IOException;
}
