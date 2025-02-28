package com.codegym.repository;

import com.codegym.model.HouseMaintenance;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface IHouseMaintenanceRepository extends JpaRepository<HouseMaintenance, Long> {
    List<HouseMaintenance> findByHouseId(Long houseId);

    boolean existsByHouseIdAndStartDateAndEndDate(Long houseId, LocalDate startDate, LocalDate endDate);
}
