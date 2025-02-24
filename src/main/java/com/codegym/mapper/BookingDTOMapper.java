package com.codegym.mapper;

import com.codegym.model.Booking;
import com.codegym.model.House;
import com.codegym.model.User;
import com.codegym.model.constants.BookingStatus;
import com.codegym.model.dto.BookingDTO;
import com.codegym.service.house.IHouseService;
import com.codegym.service.user.IUserService;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Optional;

@Mapper(componentModel = "spring", uses = {IHouseService.class, IUserService.class})
public abstract class BookingDTOMapper {
    @Autowired
    protected IHouseService houseService;
    @Autowired
    protected IUserService userService;

    public Booking toBooking(BookingDTO bookingDTO) {
        Booking booking = new Booking();
        booking.setStartDate(bookingDTO.getStartDate());
        booking.setEndDate(bookingDTO.getEndDate());
        booking.setPrice(bookingDTO.getPrice());
        booking.setUpdatedAt(LocalDateTime.now());
        booking.setStatus(BookingStatus.WAITING);
        Optional<House> house = houseService.findById(bookingDTO.getHouseId());
        if (house.isEmpty()) {
            throw new RuntimeException("Could not find house with id: " + bookingDTO.getHouseId());
        }
        Optional<User> user = userService.findById(bookingDTO.getUserId());
        if (user.isEmpty()) {
            throw new RuntimeException("Could not find user with id: " + bookingDTO.getUserId());
        }
        booking.setHouse(house.get());
        booking.setUser(user.get());
        return booking;
    };

    public BookingDTO toBookingDTO(Booking booking) {
        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setHouseId(booking.getHouse().getId());
        bookingDTO.setUserId(booking.getUser().getId());
        bookingDTO.setStartDate(booking.getStartDate());
        bookingDTO.setEndDate(booking.getEndDate());
        bookingDTO.setPrice(booking.getPrice());
        return bookingDTO;
    }
}
