package com.codegym.service.house;

import com.codegym.model.House;
import com.codegym.model.dto.HouseDTO;
import com.codegym.service.IGenerateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface IHouseService extends IGenerateService<House> {

    List<House> findAll();

    List<House> searchHousesAsc(String address, LocalDate checkIn, LocalDate checkOut, Integer minBedrooms, Integer minBathrooms, Integer minPrice, Integer maxPrice);

    List<House> searchHousesDesc(String address, LocalDate checkIn, LocalDate checkOut, Integer minBedrooms, Integer minBathrooms, Integer minPrice, Integer maxPrice);

    List<House> findHousesByHostId(Long id);

    void addHouseImages(Long id, List<MultipartFile> images) ;

    void removeHouseImage(Long id, Long imageId) ;

    HouseDTO updateHouseDetails(Long id, HouseDTO houseDTO);

    ResponseEntity<String> validateInput(HouseDTO houseDTO);
}
