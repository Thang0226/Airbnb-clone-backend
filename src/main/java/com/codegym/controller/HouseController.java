package com.codegym.controller;


import com.codegym.model.House;
import com.codegym.model.HouseStatus;
import com.codegym.model.SearchRequest;
import com.codegym.repository.IHouseRepository;
import com.codegym.service.HouseService;
import com.codegym.service.IHouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/houses")
public class HouseController {

        private final IHouseService houseService;

        private final IHouseRepository houseRepository;

        public HouseController(HouseService houseService, IHouseRepository houseRepository) {
            this.houseService = houseService;
            this.houseRepository = houseRepository;
        }
       //api phải là: http://localhost:8080/api/housesList?status=AVAILABLE thì mới lấy được dữ liệu
        @GetMapping
        public ResponseEntity<List<House>> getHousesForAvailable(@RequestParam(name = "status", required = false) HouseStatus status) {
            List<House> houses = List.of();
            if (status == null) {
                houses = houseService.findAll();
            } else {
                houses = houseService.getHousesForAVAILABLE(String.valueOf(status));
            }
            return ResponseEntity.ok(houses);
        }


    @PostMapping("/search")
    public ResponseEntity<List<House>> searchHouses(@RequestBody SearchRequest request) {
        List<House> houses = houseService.searchHouses(
                request.getCheckIn(),
                request.getCheckOut(),
                request.getGuests(),
                request.getSortOrder(),
                request.getMinBedrooms(),
                request.getMinBathrooms()
        );
        return ResponseEntity.ok(houses);
    }

}


