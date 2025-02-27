package com.codegym.service.availability;

import com.codegym.model.Availability;
import com.codegym.model.Booking;
import com.codegym.model.House;
import com.codegym.model.HouseMaintenance;
import com.codegym.service.IGenerateService;

import java.time.LocalDate;

public interface IAvailabilityService extends IGenerateService<Availability> {

    Availability getAvailabilityCoversBookingTime(Booking booking);

    LocalDate findNearestAvailableDate(House house, LocalDate date);

    LocalDate findSoonestAvailableDate(House house);

    Availability findByStartDate(House house, LocalDate date);

    Availability findByEndDate(House house, LocalDate date);

    Availability getAvailabilityCoversHouseMaintenance(HouseMaintenance houseMaintenance);

    void createTwoNewFromAnOldOne(LocalDate startDate, LocalDate endDate, Availability availability);
}
