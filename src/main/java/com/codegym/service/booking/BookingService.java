package com.codegym.service.booking;
import com.codegym.mapper.BookingMapper;
import com.codegym.model.Availability;
import com.codegym.model.Booking;
import com.codegym.model.House;
import com.codegym.model.dto.booking.BookingDTO;
import com.codegym.model.dto.user.UserRentalHistoryDTO;
import com.codegym.repository.IBookingRepository;
import com.codegym.service.availability.IAvailabilityService;
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
        if (availability == null) {
            throw new RuntimeException("Can't find availability");
        }
        // 3. Remove this availability & insert two new availabilities on the two time end if not fully booked
        availabilityService.deleteById(availability.getId());
        House house = booking.getHouse();
        LocalDate bookingStartDate = booking.getStartDate();
        LocalDate bookingEndDate = booking.getEndDate();
        LocalDate availStartDate = availability.getStartDate();
        LocalDate availEndDate = availability.getEndDate();
        if (bookingStartDate.isAfter(availStartDate)) {
            Availability firstAvail = new Availability();
            firstAvail.setStartDate(availStartDate);
            firstAvail.setEndDate(bookingStartDate.minusDays(1));
            firstAvail.setHouse(house);
            availabilityService.save(firstAvail);
        }
        if (bookingEndDate.isBefore(availEndDate)) {
            Availability secondAvail = new Availability();
            secondAvail.setStartDate(bookingEndDate.plusDays(1));
            secondAvail.setEndDate(availEndDate);
            secondAvail.setHouse(house);
            availabilityService.save(secondAvail);
        }
    }

    @Override
    public void deleteById(Long id) {
        Optional<Booking> bookingOptional = bookingRepository.findById(id);
        Booking booking;
        if (bookingOptional.isEmpty()) {
            throw new RuntimeException("Cannot find booking with id: " + id);
        } else {
            booking = bookingOptional.get();
        }
        // 1. Delete booking from DB
        bookingRepository.deleteById(id);
        // 2. Find two availabilities at the two time end of booking of house & delete them
        House house = booking.getHouse();
        Availability endAvailability = availabilityService.findByStartDate(house, booking.getEndDate().plusDays(1));
        Availability startAvailability = availabilityService.findByEndDate(house, booking.getStartDate().minusDays(1));
        availabilityService.deleteById(startAvailability.getId());
        availabilityService.deleteById(endAvailability.getId());
        // 3. Create new availability record that covers all old availability time & save to DB
        Availability newAvailability = new Availability();
        newAvailability.setStartDate(startAvailability.getStartDate());
        newAvailability.setEndDate(endAvailability.getEndDate());
        newAvailability.setHouse(house);
        availabilityService.save(newAvailability);
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
        return bookingRepository.findAllByHouseId(houseId);
    }

    @Override
    public Page<BookingDTO> getAllBookings(Pageable pageable) {
        Page<Booking> bookings = bookingRepository.findAll(pageable);
        return bookings.map(bookingMapper::toBookingDTO);
    }

    @Override
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
  
    @Override
    public List<Booking> findAllByUserId(Long userId) {
        return bookingRepository.findAllByUserId(userId);
    }
}
