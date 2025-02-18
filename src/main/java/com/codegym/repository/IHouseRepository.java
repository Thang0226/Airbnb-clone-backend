package com.codegym.repository;

import com.codegym.model.House;
import com.codegym.model.SortOrder;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface IHouseRepository extends JpaRepository<House, Long> {

  @Query(nativeQuery = true, value = "call search_houses(:address, :checkIn, :checkOut, :minBedrooms, :minBathrooms, :minPrice, :maxPrice, :priceOrder)")
  List<House> searchHouses(
          @Param("address") String address,
          @Param("checkIn") LocalDate checkIn,
          @Param("checkOut") LocalDate checkOut,
          @Param("minBedrooms") Integer minBedrooms,
          @Param("minBathrooms") Integer minBathrooms,
          @Param("minPrice") Integer minPrice,
          @Param("maxPrice") Integer maxPrice,
          @Param("priceOrder") SortOrder priceOrder);
}
