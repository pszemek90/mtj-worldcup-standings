package com.pszemek.mtjworldcupstandings.entity;

import javax.persistence.*;
import java.math.BigDecimal;

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
}
