package com.codegym.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class UserRentalHistoryDTO {
    private Long id;
    private String houseName;
    private BigDecimal rentalPrice;
    private LocalDate startDate;
    private LocalDate endDate;
    private long rentalDay;
    private BigDecimal rentPaid;
}
