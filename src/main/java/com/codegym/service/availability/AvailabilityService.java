package com.codegym.service.availability;

import com.codegym.model.Availability;
import com.codegym.model.Booking;
import com.codegym.model.House;
import com.codegym.model.HouseMaintenance;
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

    @Override
    public LocalDate findSoonestAvailableDate(House house) {
        Availability soonestAvailability = availabilityRepository.findTopByHouseOrderByStartDateAsc(house);
        return soonestAvailability.getStartDate();
    }

    @Override
    public Availability findByStartDate(House house, LocalDate date) {
        return availabilityRepository.findByHouseAndStartDate(house, date);
    }

    @Override
    public Availability findByEndDate(House house, LocalDate date) {
        return availabilityRepository.findByHouseAndEndDate(house, date);
    }

    @Override
    public Availability getAvailabilityCoversHouseMaintenance(HouseMaintenance houseMaintenance) {
        LocalDate startDate = houseMaintenance.getStartDate();
        LocalDate endDate = houseMaintenance.getEndDate();
        House house = houseMaintenance.getHouse();
        return availabilityRepository.findByDateRange(startDate, endDate, house);
    }

    @Override
    public void createTwoNewFromAnOldOne(LocalDate startDate, LocalDate endDate, Availability availability) {
        availabilityRepository.deleteById(availability.getId());
        House house = availability.getHouse();
        LocalDate availStartDate = availability.getStartDate();
        LocalDate availEndDate = availability.getEndDate();
        if (startDate.isAfter(availStartDate)) {
            Availability firstAvail = new Availability();
            firstAvail.setStartDate(availStartDate);
            firstAvail.setEndDate(startDate.minusDays(1));
            firstAvail.setHouse(house);
            availabilityRepository.save(firstAvail);
        }
        if (endDate.isBefore(availEndDate)) {
            Availability secondAvail = new Availability();
            secondAvail.setStartDate(endDate.plusDays(1));
            secondAvail.setEndDate(availEndDate);
            secondAvail.setHouse(house);
            availabilityRepository.save(secondAvail);
        }
    }
}
