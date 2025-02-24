package com.codegym.model.dto;

import com.codegym.model.constants.BookingStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class BookingDTO {
    private Long id;
    private String houseName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long rentalDay;
    private String customerName;
    private BigDecimal totalCost;
    private BookingStatus status;
}
