package com.codegym.model.dto.house;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TopFiveHousesDTO {
    private Long id;
    private String houseName;
    private BigDecimal price;
    private String address;
    private String image;
}
