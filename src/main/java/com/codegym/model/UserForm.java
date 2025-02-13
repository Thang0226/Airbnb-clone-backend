package com.codegym.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
public class UserForm {
    private Long id;
    private String username;
    private MultipartFile avatar;
    private String fullName;
    private String address;
    private String phone;
}
