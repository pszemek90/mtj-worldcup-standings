package com.pszemek.mtjworldcupstandings.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class AccountHistoryPageRequest {
    @JsonProperty("history")
    private List<AccountHistoryDto> history;
    @JsonProperty("total")
    private Long totalAmount;

    public AccountHistoryPageRequest setHistory(List<AccountHistoryDto> history) {
        this.history = history;
        return this;
    }

    public AccountHistoryPageRequest setTotalAmount(Long totalAmount) {
        this.totalAmount = totalAmount;
        return this;
    }
}
