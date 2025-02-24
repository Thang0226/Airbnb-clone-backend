package com.codegym.model.dto;

import java.math.BigDecimal;

public interface HostInfoDTO {
    Long getId();
    String getUsername();
    String getStatus();
    String getFullName();
    String getAddress();
    String getPhone();
    Integer getHousesForRent();
    BigDecimal getTotalIncome();
}
