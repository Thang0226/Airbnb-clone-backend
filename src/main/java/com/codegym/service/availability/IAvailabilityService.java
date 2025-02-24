package com.codegym.service.availability;

import com.codegym.model.Availability;
import com.codegym.model.Booking;
import com.codegym.service.IGenerateService;

public interface IAvailabilityService extends IGenerateService<Availability> {

    Availability getAvailabilityCoversBookingTime(Booking booking);

}
