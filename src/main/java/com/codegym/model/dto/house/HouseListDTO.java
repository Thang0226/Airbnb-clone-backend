package com.codegym.model.dto.house;

import java.math.BigDecimal;

public interface HouseListDTO {
    Long getId();
    String getHouseName();
    BigDecimal getPrice();
    String getAddress();
    BigDecimal getTotalRevenue();
    String getStatus();
    int getRentals();
}
