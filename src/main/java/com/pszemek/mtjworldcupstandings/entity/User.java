package com.pszemek.mtjworldcupstandings.entity;

import lombok.Getter;

import javax.persistence.*;
import java.math.BigDecimal;

@Getter
@Entity(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "username")
    private String username;
    @Column(name = "password")
    private String password;
    @Column(name = "email")
    private String email;
    @Column(name = "balance")
    private BigDecimal balance;
    @Column(name = "country")
    private String country;

    public User setBalance(BigDecimal balance) {
        this.balance = balance;
        return this;
    }

    public User setCountry(String country) {
        this.country = country;
        return this;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }
}
