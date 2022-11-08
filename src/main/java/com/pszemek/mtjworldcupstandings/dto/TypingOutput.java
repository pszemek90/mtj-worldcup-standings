package com.pszemek.mtjworldcupstandings.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class TypingOutput {
    @JsonProperty("homeTeam")
    private String homeTeam;
    @JsonProperty("result")
    private String result;
    @JsonProperty("awayTeam")
    private String awayTeam;
    @JsonProperty("status")
    private String status;

    public TypingOutput(String homeTeam, String result, String awayTeam, String status) {
        this.homeTeam = homeTeam;
        this.result = result;
        this.awayTeam = awayTeam;
        this.status = status;
    }
}
