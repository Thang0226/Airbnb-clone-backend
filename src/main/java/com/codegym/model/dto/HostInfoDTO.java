package com.codegym.model.dto;

import com.codegym.model.constants.UserStatus;

import java.math.BigDecimal;

public class HostInfoDTO {
    private Long id;
    private String username;
    private UserStatus status;
    private String fullName;
    private String phone;
    private int housesForRent;
    private BigDecimal income;
}
