package com.codegym.model.dto.user;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserProfileDTO {
    private String username;
    private String avatar;
    private String fullName;
    private String address;
    private String phone;
}
