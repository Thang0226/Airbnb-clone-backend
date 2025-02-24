package com.codegym.controller;

import com.codegym.model.dto.BookingDTO;
import com.codegym.service.booking.IBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/bookings")
public class BookingController {
    @Autowired
    private IBookingService bookingService;

    @GetMapping
    public ResponseEntity<PagedModel<?>> getBookings(Pageable pageable, PagedResourcesAssembler<BookingDTO> assembler) {
        Page<BookingDTO> bookings = bookingService.getAllBookings(pageable);
        return new ResponseEntity<>(assembler.toModel(bookings), HttpStatus.OK);
    }
}
