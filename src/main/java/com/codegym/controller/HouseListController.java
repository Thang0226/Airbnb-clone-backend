package com.codegym.controller;


import com.codegym.model.HouseList;
import com.codegym.model.HouseListStatusEnum;
import com.codegym.service.HouseListService;
import com.codegym.service.IHouseListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/housesList")
public class HouseListController {

        @Autowired
        private final IHouseListService houseListService;

        public HouseListController(HouseListService houseListService) {
            this.houseListService = houseListService;
        }
       //api phải là: http://localhost:8080/api/housesList/for-AVAILABLE?status=AVAILABLE thì mới lấy được dữ liệu
        @GetMapping("/for-AVAILABLE")
        public ResponseEntity<List<HouseList>> getHousesForRented(@RequestParam HouseListStatusEnum status) {
            List<HouseList> houseLists = houseListService.getHousesForAVAILABLE(String.valueOf(status));
            return ResponseEntity.ok(houseLists);
        }

    }


