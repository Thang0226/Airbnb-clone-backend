package com.codegym.controller;


import com.codegym.model.House;
import com.codegym.model.HouseStatus;
import com.codegym.service.HouseService;
import com.codegym.service.IHouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/houses")
public class HouseController {

        private final IHouseService houseService;

        public HouseController(HouseService houseService) {
            this.houseService = houseService;
        }
       //api phải là: http://localhost:8080/api/housesList/for-AVAILABLE?status=AVAILABLE thì mới lấy được dữ liệu
        @GetMapping
        public ResponseEntity<List<House>> getHousesForRented(@RequestParam(name = "status", required = false) HouseStatus status) {
            List<House> houses = List.of();
            if (status == null) {
                houses = houseService.findAll();
            } else {
                houses = houseService.getHousesForAVAILABLE(String.valueOf(status));
            }
            return ResponseEntity.ok(houses);
        }

    }


