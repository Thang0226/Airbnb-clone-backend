package com.codegym.service;


import com.codegym.model.House;
import com.codegym.model.HouseStatus;
import com.codegym.repository.IHouseRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;

@Service
public class HouseService implements IHouseService {


    private final IHouseRepository houseRepository;

    public HouseService(IHouseRepository houseRepository) {

        this.houseRepository = houseRepository;
    }

    public List<House> getHousesForAVAILABLE(String status) {
        try {
            HouseStatus statusEnum = HouseStatus.valueOf(status.toUpperCase());
            return houseRepository.findByStatus(statusEnum);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status value: " + status);
        }
    }

    @Override
    public List<House> findAll() {
        return houseRepository.findAll();
    }
//    public List<House> searchHouses(Specification<House> spec, Sort sort);

    public List<House> searchHouses(LocalDate checkIn, LocalDate checkOut, Integer guests, String sortOrder, Integer minBedrooms, Integer minBathrooms) {
        Specification<House> spec = Specification.where(null);

        // Lọc theo ngày
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

        // Lọc theo trạng thái AVAILABLE
        spec = spec.and((root, query, cb) ->
                cb.equal(root.get("status"), HouseStatus.AVAILABLE)
        );

        // Sắp xếp theo giá
        Sort sort = Sort.by("price");
        if ("desc".equalsIgnoreCase(sortOrder)) {
            sort = sort.descending();
        } else {
            sort = sort.ascending();
        }

        return houseRepository.findAll(spec, sort);
    }
}


