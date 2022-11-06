package com.pszemek.mtjworldcupstandings.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private BigDecimal balance;
    private String country;
    @JsonProperty("isFirstLogin")
    private boolean isFirstLogin;

    public JwtResponse(String token, Long id, String username, String email, BigDecimal balance, boolean isFirstLogin, String country) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.balance = balance;
        this.isFirstLogin = isFirstLogin;
        this.country = country;
    }
}
