package com.pszemek.mtjworldcupstandings.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class AccountHistoryDto {
    @JsonProperty("message")
    private String message;
    @JsonProperty("timestamp")
    private String timestamp;
    @JsonProperty("difference")
    private BigDecimal difference;

    public AccountHistoryDto setMessage(String message) {
        this.message = message;
        return this;
    }

    public AccountHistoryDto setTimestamp(String timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public AccountHistoryDto setDifference(BigDecimal difference) {
        this.difference = difference;
        return this;
    }
}
