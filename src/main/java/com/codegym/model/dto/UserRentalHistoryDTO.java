package com.codegym.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

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

    public void calcRentPaid(LocalDate startDate, LocalDate endDate, int housePrice) {
        long days = ChronoUnit.DAYS.between(startDate, endDate);
        days = Math.max(days, 0);
        this.rentalDay = days;
        BigDecimal pricePerDay = BigDecimal.valueOf(housePrice);
        this.rentPaid = pricePerDay.multiply(BigDecimal.valueOf(days));
    }
}
