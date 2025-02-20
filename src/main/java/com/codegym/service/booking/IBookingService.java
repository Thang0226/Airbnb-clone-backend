package com.codegym.service.booking;

import com.codegym.model.Booking;
import com.codegym.service.IGenerateService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IBookingService extends IGenerateService<Booking> {
    Page<Booking> getBookingsByUserId(Long userId, Pageable pageable);
}
