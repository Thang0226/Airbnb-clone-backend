package com.codegym.config.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
public class JwtResponse {

    @Setter
    private Long id;
    @Setter
    private String token;
    @Setter
    private String type = "Bearer";
    @Setter
    private String username;
    @Setter
    private String name;

    private final Collection<? extends GrantedAuthority> authorities;

    public JwtResponse(Long id, String token, String username, String name, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.token = token;
        this.username = username;
        this.name = name;
        this.authorities = authorities;
    }
}
