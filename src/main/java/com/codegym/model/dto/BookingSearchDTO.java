package com.codegym.model.dto;

import com.codegym.model.constants.BookingStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class BookingSearchDTO {
    private String houseName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
}
