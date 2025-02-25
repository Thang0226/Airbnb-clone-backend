package com.codegym.model.dto;

import com.codegym.model.constants.BookingStatus;
import com.codegym.model.constants.HouseStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class UserBookingDTO {
    private Long id;
    private String houseName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String address;
    private Integer totalCost;
    private BookingStatus bookingStatus;
    private HouseStatus houseStatus;
}
