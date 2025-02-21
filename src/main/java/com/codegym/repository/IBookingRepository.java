package com.codegym.repository;

import com.codegym.model.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface IBookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking b JOIN b.user u WHERE u.id = :userId AND b.status = 'CONFIRMED'")
    Page<Booking> findBookingsByUserId(@Param("userId")Long userId, Pageable pageable);

    @Query("SELECT COALESCE(SUM((DATEDIFF(b.endDate, b.startDate)) * h.price), 0) " +
            "FROM Booking b JOIN b.user u JOIN b.house h WHERE u.id = :userId AND b.status = 'CONFIRMED'")
    BigDecimal getTotalRentPaidByUserId(@Param("userId")Long userId);
}
