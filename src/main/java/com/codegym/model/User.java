package com.codegym.model;
import com.codegym.model.auth.Role;
import com.codegym.model.constants.UserStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    private String avatar;

    private String fullName;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(unique = true)
    private String phone;

    @Column(unique = true)
    private String email;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Role> roles;

    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.ACTIVE;
}
