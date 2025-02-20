package com.codegym.service.booking;
import com.codegym.model.Booking;
import com.codegym.repository.IBookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookingService implements IBookingService {
    @Autowired
    private IBookingRepository bookingRepository;

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
}
