package com.codegym.repository;

import com.codegym.model.Availability;
import com.codegym.model.House;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface IAvailabilityRepository extends JpaRepository<Availability, Long> {

    @Query(value="SELECT a FROM Availability a WHERE a.house = :house AND a.startDate <= :startDate AND :endDate <= a.endDate")
    Availability findByDateRange(@Param("startDate") LocalDate startDate,
                                 @Param("endDate") LocalDate endDate,
                                 @Param("house") House house);

    @Query(value="SELECT a.endDate FROM Availability a WHERE a.house = :house AND a.startDate <= :date AND :date <= a.endDate")
    LocalDate findNearestAvailableDate(House house, LocalDate date);

    Availability findTopByHouseOrderByStartDateAsc(House house);

    Availability findByHouseAndStartDate(House house, LocalDate startDate);

    Availability findByHouseAndEndDate(House house, LocalDate endDate);
}
