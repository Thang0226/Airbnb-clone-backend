package com.codegym.mapper;

import com.codegym.model.Booking;
import com.codegym.model.House;
import com.codegym.model.User;
import com.codegym.model.constants.BookingStatus;
import com.codegym.model.dto.booking.BookingDTOForReview;
import com.codegym.model.dto.booking.NewBookingDTO;
import com.codegym.model.dto.user.UserBookingDTO;
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

    public Booking toBooking(NewBookingDTO newBookingDTO) {
        Booking booking = new Booking();
        booking.setStartDate(newBookingDTO.getStartDate());
        booking.setEndDate(newBookingDTO.getEndDate());
        booking.setPrice(newBookingDTO.getPrice());
        booking.setUpdatedAt(LocalDateTime.now());
        booking.setStatus(BookingStatus.WAITING);
        Optional<House> house = houseService.findById(newBookingDTO.getHouseId());
        if (house.isEmpty()) {
            throw new RuntimeException("Could not find house with id: " + newBookingDTO.getHouseId());
        }
        Optional<User> user = userService.findById(newBookingDTO.getUserId());
        if (user.isEmpty()) {
            throw new RuntimeException("Could not find user with id: " + newBookingDTO.getUserId());
        }
        booking.setHouse(house.get());
        booking.setUser(user.get());
        return booking;
    };

    public NewBookingDTO toNewBookingDTO(Booking booking) {
        NewBookingDTO newBookingDTO = new NewBookingDTO();
        newBookingDTO.setHouseId(booking.getHouse().getId());
        newBookingDTO.setUserId(booking.getUser().getId());
        newBookingDTO.setStartDate(booking.getStartDate());
        newBookingDTO.setEndDate(booking.getEndDate());
        newBookingDTO.setPrice(booking.getPrice());
        return newBookingDTO;
    }

    public UserBookingDTO toUserBookingDTO(Booking booking) {
        UserBookingDTO userBookingDTO = new UserBookingDTO();
        userBookingDTO.setId(booking.getId());
        userBookingDTO.setBookingStatus(booking.getStatus());
        userBookingDTO.setStartDate(booking.getStartDate());
        userBookingDTO.setEndDate(booking.getEndDate());
        userBookingDTO.setTotalCost(booking.getPrice());
        House house = booking.getHouse();
        userBookingDTO.setAddress(house.getAddress());
        userBookingDTO.setHouseName(house.getHouseName());
        userBookingDTO.setHouseStatus(house.getStatus());
        return userBookingDTO;
    }

    public BookingDTOForReview toBookingDTOForReview(Booking booking) {
        BookingDTOForReview bookingDTOForReview = new BookingDTOForReview();
        bookingDTOForReview.setId(booking.getId());
        bookingDTOForReview.setHouseName(booking.getHouse().getHouseName());
        bookingDTOForReview.setCustomerName(booking.getUser().getFullName());
        bookingDTOForReview.setCustomerImage(booking.getUser().getAvatar());
        return bookingDTOForReview;
    }
}
