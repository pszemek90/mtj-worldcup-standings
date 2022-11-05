package com.pszemek.mtjworldcupstandings.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class OverallPoolDto {
    @JsonProperty("overallPool")
    private BigDecimal overallPool;

    public OverallPoolDto setOverallPool(BigDecimal overallPool) {
        this.overallPool = overallPool;
        return this;
    }
}
