package com.pszemek.mtjworldcupstandings.dto;

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

    public JwtResponse(String token, Long id, String username, String email, BigDecimal balance) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.balance = balance;
    }
}
