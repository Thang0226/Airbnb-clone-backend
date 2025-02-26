package com.codegym.controller;

import com.codegym.mapper.BookingDTOMapper;
import com.codegym.model.Booking;
import com.codegym.model.User;
import com.codegym.model.dto.booking.BookingDTO;
import com.codegym.model.dto.booking.BookingSearchDTO;
import com.codegym.model.dto.user.UserBookingDTO;
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
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/bookings")
public class BookingController {
    @Autowired
    private IBookingService bookingService;
    @Autowired
    private BookingDTOMapper bookingDTOMapper;

    @Autowired
    private IUserService userService;

    @GetMapping
    public ResponseEntity<PagedModel<?>> getBookings(Pageable pageable, PagedResourcesAssembler<BookingDTO> assembler) {
        Page<BookingDTO> bookings = bookingService.getAllBookings(pageable);
        return new ResponseEntity<>(assembler.toModel(bookings), HttpStatus.OK);
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getBookingsByUsernameOfHost(@PathVariable String username, Pageable pageable) {
        Optional<User> user = userService.findByUsername(username);
        if (user.isEmpty()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        Page<BookingDTO> bookings = bookingService.getAllBookingsByHostId(user.get().getId(),pageable);
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    @PostMapping("/{username}/search")
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

    @GetMapping("/user/{username}")
    public ResponseEntity<?> getBookingsByUsernameOfUser(@PathVariable String username) {
        Optional<User> userOptional = userService.findByUsername(username);
        if (userOptional.isEmpty()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        List<Booking> bookings = bookingService.findAllByUserId(userOptional.get().getId());
        List<UserBookingDTO> userBookings = bookings.stream().map(booking -> bookingDTOMapper.toUserBookingDTO(booking)).toList();
        return new ResponseEntity<>(userBookings, HttpStatus.OK);
    }
}
