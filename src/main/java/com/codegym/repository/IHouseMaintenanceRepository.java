package com.codegym.repository;

import com.codegym.model.HouseMaintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface IHouseMaintenanceRepository extends JpaRepository<HouseMaintenance, Long> {
    List<HouseMaintenance> findByHouseId(Long houseId);

    @Query("""
            SELECT CASE WHEN COUNT(hm) > 0 THEN true ELSE false END
            FROM HouseMaintenance hm
            WHERE hm.house.id = :houseId
            AND (hm.startDate <= :endDate AND hm.endDate >= :startDate)
            """)
    boolean overlappingMaintenance(@Param("houseId") Long houseId,
                                         @Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);

}
