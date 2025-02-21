package com.codegym.model.dto;

import com.codegym.model.constants.UserStatus;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class UserInfoDTO {
    private Long id;
    private String username;
    private String phone;
    private UserStatus status;
}