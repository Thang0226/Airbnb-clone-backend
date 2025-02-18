package com.codegym.repository;

import com.codegym.model.House;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.codegym.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface IHouseRepository extends JpaRepository<House, Long>, JpaSpecificationExecutor<House> {

  @Query("SELECT h FROM House h " +
          "WHERE (:minBedrooms IS NULL OR h.bedrooms >= :minBedrooms) " +
          "AND (:minBathrooms IS NULL OR h.bathrooms >= :minBathrooms) " +
          "AND (:address IS NULL OR TRIM(:address) = '' OR " +
          "LOWER(h.address) LIKE LOWER(CONCAT('%', :address, '%')))")
  List<House> searchHouses(@Param("checkIn") LocalDate checkIn,
                           @Param("checkOut") LocalDate checkOut,
                           @Param("minBedrooms") Integer minBedrooms,
                           @Param("minBathrooms") Integer minBathrooms,
                           @Param("address") String address,
                           Sort sort);
}
