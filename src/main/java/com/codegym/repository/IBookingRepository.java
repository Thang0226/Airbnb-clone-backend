package com.codegym.repository;

import com.codegym.model.Booking;
import com.codegym.model.constants.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface IBookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking b JOIN b.user u WHERE u.id = :userId AND b.status = 'CHECKED_OUT'")
    Page<Booking> findBookingsByUserId(@Param("userId")Long userId, Pageable pageable);

    @Query("SELECT COALESCE(SUM((DATEDIFF(b.endDate, b.startDate)) * b.price), 0) " +
            "FROM Booking b JOIN b.user u JOIN b.house h WHERE u.id = :userId AND b.status = 'CHECKED_OUT'")
    BigDecimal getTotalRentPaidByUserId(@Param("userId")Long userId);

    List<Booking> findAllByHouseId(Long houseId);

    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId ORDER BY b.startDate DESC")
    List<Booking> findAllByUserId(Long userId);
  
    @Query("SELECT b FROM Booking b JOIN b.house h WHERE h.host.id = :userId ORDER BY b.id DESC")
    Page<Booking> findBookingsByHostId(Long userId, Pageable pageable);

    @Query(nativeQuery = true, value = "call search_bookings_of_host( :userId, :houseName, :startDate, :endDate, :status)")
    List<Booking> searchBookingsByHostId(
            @Param("userId") Long userId,
            @Param("houseName") String houseName,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") String status);
}
