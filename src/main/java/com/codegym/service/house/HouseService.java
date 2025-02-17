package com.codegym.service.house;


import com.codegym.model.House;
import com.codegym.model.HouseStatus;
import com.codegym.repository.IHouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class HouseService implements IHouseService {

    @Autowired
    private IHouseRepository houseRepository;
    @Override
    public List<House> findAll() {
        return houseRepository.findAll();
    }

    @Override
    public Optional<House> findById(Long id) {
        return houseRepository.findById(id);
    }

    @Override
    public void save(House object) {
        houseRepository.save(object);
    }

    @Override
    public void deleteById(Long id) {
        houseRepository.deleteById(id);
    }

    public List<House> getHousesForAVAILABLE(String status) {
        try {
            HouseStatus statusEnum = HouseStatus.valueOf(status.toUpperCase());
            return houseRepository.findByStatus(statusEnum);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status value: " + status);
        }
    }

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


