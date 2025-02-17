package com.codegym.model.auth;

import com.codegym.model.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
public class UserPrincipal implements UserDetails {
    @Serial
    private static final long serialVersionUID = 1L;
    private final Long id;
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> roles;

    public UserPrincipal(Long id, String username, String password, Collection<? extends GrantedAuthority> roles){
        this.id = id;
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

    public static UserPrincipal build(User user) {
        List<GrantedAuthority> authorities = new ArrayList<>(user.getRoles());
        return new UserPrincipal(user.getId(), user.getUsername(), user.getPassword(), authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
