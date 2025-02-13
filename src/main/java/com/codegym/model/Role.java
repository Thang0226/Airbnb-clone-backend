package com.codegym.model;

import jakarta.persistence.*;
import lombok.Data;
//import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name = "roles")
@Data
//public class Role implements GrantedAuthority
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

//    @Override
//    public String getAuthority() {
//        return this.name;
//    }
}
