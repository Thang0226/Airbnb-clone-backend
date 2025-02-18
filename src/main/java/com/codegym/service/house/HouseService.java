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

    public List<House> searchHouses(String address, LocalDate checkIn, LocalDate checkOut, Integer guests, String sortOrder, Integer minBedrooms, Integer minBathrooms) {
        // Xác định sắp xếp theo giá
        Sort sort = Sort.by("price");
        if ("desc".equalsIgnoreCase(sortOrder)) {
            sort = sort.descending();
        } else {
            sort = sort.ascending();
        }

        // In ra console các tham số tìm kiếm để kiểm tra
        System.out.println("=== Bắt đầu quá trình tìm kiếm nhà ===");
        System.out.println("Địa chỉ: " + address);
        System.out.println("Ngày nhận phòng (checkIn): " + checkIn);
        System.out.println("Ngày trả phòng (checkOut): " + checkOut);
        System.out.println("Số lượng khách: " + guests);
        System.out.println("Sắp xếp theo giá: " + sortOrder);
        System.out.println("Phòng ngủ tối thiểu: " + minBedrooms);
        System.out.println("Phòng tắm tối thiểu: " + minBathrooms);
        System.out.println("Sắp xếp: " + sort);

        // Gọi repository method với các tham số lọc
        List<House> houses = houseRepository.searchHouses(checkIn, checkOut, minBedrooms, minBathrooms, address, sort);

        // In ra kết quả tìm kiếm
        System.out.println("Tìm thấy " + houses.size() + " nhà.");
        for (House house : houses) {
            System.out.println("House ID: " + house.getId()
                    + " - Address: " + house.getAddress()
                    + " - Price: " + house.getPrice());
        }
        System.out.println("=== Kết thúc quá trình tìm kiếm ===");

        return houses;
    }

}


