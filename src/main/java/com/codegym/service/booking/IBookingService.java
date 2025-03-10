package com.codegym.service.booking;

import com.codegym.model.Booking;
import com.codegym.model.dto.booking.BookingDTO;
import com.codegym.model.dto.user.UserRentalHistoryDTO;
import com.codegym.service.IGenerateService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IBookingService extends IGenerateService<Booking> {
    Page<Booking> getBookingsByUserId(Long userId, Pageable pageable);

    Page<UserRentalHistoryDTO> getUserRentalHistory(Long userID, Pageable pageable);

    BigDecimal getTotalRentPaidByUserId(Long userId);

    List<Booking> getBookingsByHouseId(Long houseId);

    Page<BookingDTO> getAllBookings(Pageable pageable);

    Page<BookingDTO> getAllBookingsByHostId(Long userId, Pageable pageable);

    Page<BookingDTO> searchBookingsByHostId(
            Long userId,
            String houseName,
            LocalDate startDate,
            LocalDate endDate,
            String status,
            Pageable pageable);

    List<Booking> findAllByUserId(Long userId);

    BookingDTO processBooking(Long bookingId, String action);

    boolean overlappingBooking(Long houseId, LocalDate startDate, LocalDate endDate);
}
