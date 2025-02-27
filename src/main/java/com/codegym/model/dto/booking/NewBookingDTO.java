package com.codegym.model.dto.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewBookingDTO {
    private Long houseId;
    private Long userId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer price;
}
