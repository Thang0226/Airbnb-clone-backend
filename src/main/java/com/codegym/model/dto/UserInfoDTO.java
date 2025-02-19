package com.codegym.model.dto;

import com.codegym.model.User.Status; // Import enum Status từ entity User
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class UserInfoDTO {
    private Long id;
    private String username;
    private String phone;
    private Status status;
}