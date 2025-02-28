package com.codegym.model.dto.host;

import java.math.BigDecimal;

public interface HostInfoDTO {
    Long getId();
    String getUsername();
    String getAvatar();
    String getStatus();
    String getFullName();
    String getAddress();
    String getPhone();
    Integer getHousesForRent();
    BigDecimal getTotalIncome();
}
