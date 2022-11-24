package com.pszemek.mtjworldcupstandings.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class TyperScore {
    @JsonProperty("username")
    private String username;
    @JsonProperty("correctTypings")
    private Integer correctTypings;
    @JsonProperty("balance")
    private BigDecimal balance;
    @JsonProperty("country")
    private String country;

    public TyperScore setUsername(String username) {
        this.username = username;
        return this;
    }

    public TyperScore setCorrectTypings(Integer correctTypings) {
        this.correctTypings = correctTypings;
        return this;
    }

    public TyperScore setBalance(BigDecimal balance) {
        this.balance = balance;
        return this;
    }

    public TyperScore setCountry(String country) {
        this.country = country;
        return this;
    }
}
