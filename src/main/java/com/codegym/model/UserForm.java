package com.codegym.model;

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
    private String username;
    private MultipartFile avatar;
    private String fullName;
    private String address;
    private String phone;
}
