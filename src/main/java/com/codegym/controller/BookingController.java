package com.codegym.controller;

import com.codegym.model.User;
import com.codegym.model.dto.BookingDTO;
import com.codegym.model.dto.BookingSearchDTO;
import com.codegym.service.booking.IBookingService;
import com.codegym.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/bookings")
public class BookingController {
    @Autowired
    private IBookingService bookingService;

    @Autowired
    private IUserService userService;

    @GetMapping
    public ResponseEntity<PagedModel<?>> getBookings(Pageable pageable, PagedResourcesAssembler<BookingDTO> assembler) {
        Page<BookingDTO> bookings = bookingService.getAllBookings(pageable);
        return new ResponseEntity<>(assembler.toModel(bookings), HttpStatus.OK);
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getBookingsByUserName(@PathVariable String username, Pageable pageable) {
        Optional<User> user = userService.findByUsername(username);
        if (user.isEmpty()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        Page<BookingDTO> bookings = bookingService.getAllBookingsByHostId(user.get().getId(),pageable);
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    @PostMapping("/search/{username}")
    public ResponseEntity<?> searchBookings(
            @RequestBody BookingSearchDTO bookingSearchDTO,
            @PathVariable String username,
            Pageable pageable) {
        Optional<User> user = userService.findByUsername(username);
        if (user.isEmpty()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        Page<BookingDTO> bookings = bookingService.searchBookingsByHostId(
                user.get().getId(),
                bookingSearchDTO.getHouseName(),
                bookingSearchDTO.getStartDate(),
                bookingSearchDTO.getEndDate(),
                bookingSearchDTO.getStatus(),
                pageable
        );
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }
}
