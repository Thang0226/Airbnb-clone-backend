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
        // Nhận dữ liệu từ request body
        LocalDate checkIn = request.getCheckIn();
        LocalDate checkOut = request.getCheckOut();
        Integer guests = request.getGuests();
        String sortOrder = request.getSortOrder();
        Integer minBedrooms = request.getMinBedrooms();
        Integer minBathrooms = request.getMinBathrooms();

        System.out.println("Received parameters:");
        System.out.println("Check-in: " + checkIn);
        System.out.println("Check-out: " + checkOut);
        System.out.println("Guests: " + guests);
        System.out.println("Sort Order: " + sortOrder);
        System.out.println("Min Bedrooms: " + minBedrooms);
        System.out.println("Min Bathrooms: " + minBathrooms);

        Specification<House> spec = Specification.where(null);


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
                    cb.greaterThanOrEqualTo(root.get("bedrooms"), minBedrooms));
        }

        // Lọc theo số phòng tắm tối thiểu
        if (minBathrooms != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("bathrooms"), minBathrooms));
        }

        // *** Lọc theo status: chỉ lấy những căn nhà có status AVAILABLE ***
        spec = spec.and((root, query, cb) ->
                cb.equal(root.get("status"), HouseStatus.AVAILABLE)
        );



        // Xử lý sắp xếp theo giá tiền theo trường pricePerDay
        Sort sort = Sort.by("price");
        if ("desc".equalsIgnoreCase(sortOrder)) {
            sort = sort.descending();
        } else {
            sort = sort.ascending();
        }

        List<House> houses = houseService.findAlltoSearch(spec, sort);
        return ResponseEntity.ok(houses);
    }

}


