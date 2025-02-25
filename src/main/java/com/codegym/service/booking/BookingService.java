package com.codegym.service.booking;
import com.codegym.mapper.BookingMapper;
import com.codegym.model.Booking;
import com.codegym.model.constants.BookingStatus;
import com.codegym.model.dto.BookingDTO;
import com.codegym.model.dto.UserRentalHistoryDTO;
import com.codegym.repository.IBookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public  class BookingService implements IBookingService {
    @Autowired
    private IBookingRepository bookingRepository;

    @Autowired
    private BookingMapper bookingMapper;

    @Override
    public Iterable<Booking> findAll() {
        return bookingRepository.findAll();
    }

    @Override
    public Optional<Booking> findById(Long id) {
        return bookingRepository.findById(id);
    }

    @Override
    public void save(Booking booking) {
        bookingRepository.save(booking);
    }

    @Override
    public void deleteById(Long id) {
        bookingRepository.deleteById(id);
    }

    @Override
    public Page<Booking> getBookingsByUserId(Long userId, Pageable pageable) {
        return bookingRepository.findBookingsByUserId(userId, pageable);
    }

    @Override
    public Page<UserRentalHistoryDTO> getUserRentalHistory(Long userID, Pageable pageable) {
        Page<Booking> bookings = getBookingsByUserId(userID, pageable);
        return bookings.map(bookingMapper::toUserRentalHistoryDTO);
    }

    @Override
    public BigDecimal getTotalRentPaidByUserId(Long userId) {
        return bookingRepository.getTotalRentPaidByUserId(userId);
    }

    @Override
    public Page<BookingDTO> getAllBookings(Pageable pageable) {
        Page<Booking> bookings = bookingRepository.findAll(pageable);
        return bookings.map(bookingMapper::toBookingDTO);
    }

    public Page<BookingDTO> getAllBookingsByHostId(Long userId, Pageable pageable) {
        Page<Booking> bookings = bookingRepository.findBookingsByHostId(userId, pageable);
        return bookings.map(bookingMapper::toBookingDTO);
    }

    public Page<BookingDTO> searchBookingsByHostId( Long userId,
                                                    String houseName,
                                                    LocalDate startDate,
                                                    LocalDate endDate,
                                                    String status,
                                                    Pageable pageable) {
        List<Booking> bookings = bookingRepository.searchBookingsByHostId(
                userId, houseName, startDate, endDate, status);
        List<BookingDTO> bookingDTOs = bookings.stream()
                .map(bookingMapper::toBookingDTO)
                .toList();

        return new PageImpl<>(bookingDTOs, pageable, bookings.size());
    }
}
