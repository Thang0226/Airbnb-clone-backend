package com.codegym.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@Getter
@Setter
public class UserForm {
    private Long id;

    @NotBlank(message = "Username is required")
    private String username;

    private MultipartFile avatar;

    @NotBlank(message = "Full Name is required")
    private String fullName;

    private String address;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^0[0-9]{9}$", message = "Phone number must start with 0 and have at least 10 digits")
    private String phone;

    @NotBlank(message = "Email is required")
    @Email
    private String email;
}
