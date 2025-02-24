package com.codegym.mapper;

import com.codegym.model.Booking;
import com.codegym.model.dto.BookingDTO;
import com.codegym.model.dto.UserRentalHistoryDTO;
import org.mapstruct.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Mapper(componentModel = "spring", imports = { BigDecimal.class })
public interface BookingMapper {

    @Mapping(target = "houseName", expression = "java(booking.getHouse().getHouseName())")
    @Mapping(target = "rentalPrice", expression = "java(BigDecimal.valueOf(booking.getPrice()))")
    @Mapping(target = "rentalDay", expression = "java(calculateRentalDays(booking.getStartDate(), booking.getEndDate()))")
    @Mapping(target = "rentPaid", expression = "java(calculateRentPaid(booking.getStartDate(), booking.getEndDate(), booking.getPrice()))")
    UserRentalHistoryDTO toUserRentalHistoryDTO(Booking booking);

    @Mapping(target = "houseName", expression = "java(booking.getHouse().getHouseName())")
    @Mapping(target = "customerName", expression = "java(booking.getUser().getFullName())")
    @Mapping(target = "rentalDay", expression = "java(calculateRentalDays(booking.getStartDate(), booking.getEndDate()))")
    @Mapping(target = "totalCost", expression = "java(calculateRentPaid(booking.getStartDate(), booking.getEndDate(), booking.getPrice()))")
    BookingDTO toBookingDTO(Booking booking);

    default long calculateRentalDays(LocalDate startDate, LocalDate endDate) {
        return startDate != null && endDate != null ? Math.max(ChronoUnit.DAYS.between(startDate, endDate), 0) : 0;
    }

    default BigDecimal calculateRentPaid(LocalDate startDate, LocalDate endDate, int housePrice) {
        long days = calculateRentalDays(startDate, endDate);
        return BigDecimal.valueOf(housePrice).multiply(BigDecimal.valueOf(days));
    }
}
