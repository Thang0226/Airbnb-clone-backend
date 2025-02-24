package com.codegym.service.availability;

import com.codegym.model.Availability;
import com.codegym.model.Booking;
import com.codegym.model.House;
import com.codegym.repository.IAvailabilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class AvailabilityService implements IAvailabilityService {
    @Autowired
    private IAvailabilityRepository availabilityRepository;

    @Override
    public Iterable<Availability> findAll() {
        return availabilityRepository.findAll();
    }

    @Override
    public Optional<Availability> findById(Long id) {
        return availabilityRepository.findById(id);
    }

    @Override
    public void save(Availability object) {
        availabilityRepository.save(object);
    }

    @Override
    public void deleteById(Long id) {
        availabilityRepository.deleteById(id);
    }

    @Override
    public Availability getAvailabilityCoversBookingTime(Booking booking) {
        LocalDate startDate = booking.getStartDate();
        LocalDate endDate = booking.getEndDate();
        House bookingHouse = booking.getHouse();
        return availabilityRepository.findByDateRange(startDate, endDate, bookingHouse);
    }

    @Override
    public LocalDate findNearestAvailableDate(House house, LocalDate date) {
        return availabilityRepository.findNearestAvailableDate(house, date);
    }
}
