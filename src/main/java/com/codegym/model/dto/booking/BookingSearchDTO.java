package com.codegym.model.dto.booking;

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
