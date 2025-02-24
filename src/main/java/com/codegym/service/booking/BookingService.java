package com.codegym.service.booking;
import com.codegym.mapper.BookingMapper;
import com.codegym.model.Availability;
import com.codegym.model.Booking;
import com.codegym.model.House;
import com.codegym.model.dto.UserRentalHistoryDTO;
import com.codegym.repository.IBookingRepository;
import com.codegym.service.availability.IAvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService implements IBookingService {
    @Autowired
    private IBookingRepository bookingRepository;
    @Autowired
    private IAvailabilityService availabilityService;

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
        // 1. Save new booking (validated time conflicts)
        bookingRepository.save(booking);
        // 2. Get availability that has the time of booking
        Availability availability = availabilityService.getAvailabilityCoversBookingTime(booking);
        // 3. Remove this availability & insert two new availabilities on the two time end if not fully booked
        availabilityService.deleteById(availability.getId());
        House house = booking.getHouse();
        LocalDate bookingStartDate = booking.getStartDate();
        LocalDate bookingEndDate = booking.getEndDate();
        LocalDate availStartDate = availability.getStartDate();
        LocalDate availEndDate = availability.getEndDate();
        Availability firstAvail = new Availability();
        Availability secondAvail = new Availability();
        if (bookingStartDate.isAfter(availStartDate)) {
            firstAvail.setStartDate(availStartDate);
            firstAvail.setEndDate(bookingStartDate.minusDays(1));
            firstAvail.setHouse(house);
            availabilityService.save(firstAvail);
        }
        if (bookingEndDate.isBefore(availEndDate)) {
            secondAvail.setStartDate(bookingEndDate.plusDays(1));
            secondAvail.setEndDate(availEndDate);
            secondAvail.setHouse(house);
            availabilityService.save(secondAvail);
        }
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
    public List<Booking> getBookingsByHouseId(Long houseId) {
        return bookingRepository.findBookingsByHouseId(houseId);
    }
}
