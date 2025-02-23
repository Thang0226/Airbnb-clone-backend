package com.codegym.model.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GGAccount {

    private String username;

    private String fullName;

    private String email;

}
