package com.pszemek.mtjworldcupstandings.entity;

import lombok.Getter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity(name = "account_history")
public class AccountHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    @Column(name = "message", columnDefinition = "text")
    private String message;
    @Column(name = "timestamp")
    private LocalDateTime timestamp;
    @Column(name = "difference")
    private BigDecimal difference;
    @Column(name = "user_id")
    private Long userId;

    public AccountHistory(){}

    public AccountHistory(String message, BigDecimal difference, Long userId) {
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.difference = difference;
        this.userId = userId;
    }
}
