package com.codegym.mapper;

import com.codegym.model.Booking;
import com.codegym.model.dto.UserRentalHistoryDTO;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;


public class BookingMapper {
    public static UserRentalHistoryDTO toUserRentalHistoryDTO(Booking booking) {
        UserRentalHistoryDTO userRentalHistoryDTO = new UserRentalHistoryDTO();
        userRentalHistoryDTO.setId(booking.getId());
        userRentalHistoryDTO.setHouseName(booking.getHouse().getHouseName());
        userRentalHistoryDTO.setRentalPrice(BigDecimal.valueOf(booking.getHouse().getPrice()));
        userRentalHistoryDTO.setStartDate(booking.getStartDate());
        userRentalHistoryDTO.setEndDate(booking.getEndDate());

        userRentalHistoryDTO.calcRentPaid(booking.getStartDate(), booking.getEndDate(), booking.getHouse().getPrice());

        return userRentalHistoryDTO;
    }

    public static Page<UserRentalHistoryDTO> toUserRentalHistory(Page<Booking> bookings) {
        return bookings.map(BookingMapper::toUserRentalHistoryDTO);
    }
}
