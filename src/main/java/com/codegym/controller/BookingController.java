package com.codegym.controller;

import com.codegym.mapper.BookingDTOMapper;
import com.codegym.model.Booking;
import com.codegym.model.House;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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

    @Autowired
    private NotificationController notificationController;

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
            return new ResponseEntity<>("User not found by username: " + username, HttpStatus.NOT_FOUND);
        }
        List<Booking> bookings = bookingService.findAllByUserId(userOptional.get().getId());
        List<UserBookingDTO> userBookings = bookings.stream().map(booking -> bookingDTOMapper.toUserBookingDTO(booking)).toList();
        return new ResponseEntity<>(userBookings, HttpStatus.OK);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> deleteUserBooking(@PathVariable Long id, @RequestBody String username) {
        Optional<Booking> bookingOptional = bookingService.findById(id);
        if (bookingOptional.isEmpty()) {
            return new ResponseEntity<>("Booking not found", HttpStatus.NOT_FOUND);
        }
        Booking booking = bookingOptional.get();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = booking.getStartDate().atTime(12, 0);
        long hoursBefore = ChronoUnit.HOURS.between(now, startTime);
        if (hoursBefore < 24) {
            return new ResponseEntity<>("Not allowed to cancel booking at less than 24h before check-in time", HttpStatus.BAD_REQUEST);
        }

        bookingService.deleteById(id);
        // Notify host
        House house = booking.getHouse();
        User host = house.getHost();
        String message = '"'+booking.getUser().getUsername()+'"'+" CANCELED booking of the house "+'"'+booking.getHouse().getHouseName()+'"'
                + " on " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        notificationController.sendNotification(host, message);

        return getBookingsByUsernameOfUser(username);
    }
}
