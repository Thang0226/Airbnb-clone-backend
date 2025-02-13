package com.codegym.controller;


import com.codegym.model.House;
import com.codegym.model.HouseStatus;
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

    @GetMapping("/search")
    public ResponseEntity<List<House>> searchHouses(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam(required = false) Integer guests,  // Có thể dùng để tính toán số phòng cần có
            @RequestParam(required = false, defaultValue = "asc") String sortOrder,
            @RequestParam(required = false) Integer minBedrooms,
            @RequestParam(required = false) Integer minBathrooms
    ) {
        Specification<House> spec = Specification.where(null);

        // Lọc theo thành phố (trường city trong House)
        if (location != null && !location.trim().isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("city")), "%" + location.toLowerCase() + "%"));
        }

        // Lọc theo ngày: căn nhà phải có sẵn từ trước checkIn và còn hiệu lực sau checkOut
        if (checkIn != null && checkOut != null) {
            spec = spec.and((root, query, cb) ->
                    cb.and(
                            cb.lessThanOrEqualTo(root.get("startDate"), checkIn),
                            cb.greaterThanOrEqualTo(root.get("endDate"), checkOut)
                    )
            );
        }

        // Lọc theo số phòng ngủ tối thiểu
        if (minBedrooms != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("numberOfBedrooms"), minBedrooms));
        }

        // Lọc theo số phòng tắm tối thiểu
        if (minBathrooms != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("numberOfBathrooms"), minBathrooms));
        }

        // Xử lý sắp xếp theo giá tiền theo trường pricePerDay
        Sort sort = Sort.by("pricePerDay");
        if ("desc".equalsIgnoreCase(sortOrder)) {
            sort = sort.descending();
        } else {
            sort = sort.ascending();
        }

        List<House> houses = houseRepository.findAll(spec, sort);
        return ResponseEntity.ok(houses);
    }


    }


