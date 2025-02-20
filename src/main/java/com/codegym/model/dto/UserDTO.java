package com.codegym.model.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String username;
    private String password;
    private String phone;

    @Pattern(regexp = "^[a-z0-9._%+]+@[a-z0-9_]+.[a-z]{2,5}$", message = "Invalid email address!")
    private String email;

    private boolean host;
}
