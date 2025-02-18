package com.codegym.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchDTO {
    private String address;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private Integer minBedrooms;
    private Integer minBathrooms;
    private Integer minPrice;
    private Integer maxPrice;
    private SortOrder priceOrder;
}
