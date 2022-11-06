package com.pszemek.mtjworldcupstandings.dto;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.Collection;

@Getter
public class UserDto implements UserDetails {
    private Long id;
    private String username;
    private String password;
    private String email;
    private BigDecimal balance;
    private String country;

    public UserDto setId(Long id) {
        this.id = id;
        return this;
    }

    public UserDto setUsername(String username) {
        this.username = username;
        return this;
    }

    public UserDto setPassword(String password) {
        this.password = password;
        return this;
    }

    public UserDto setEmail(String email) {
        this.email = email;
        return this;
    }

    public UserDto setBalance(BigDecimal balance) {
        this.balance = balance;
        return this;
    }

    public UserDto setCountry(String country) {
        this.country = country;
        return this;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
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
