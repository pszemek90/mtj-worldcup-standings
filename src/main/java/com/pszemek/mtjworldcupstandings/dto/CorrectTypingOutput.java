package com.pszemek.mtjworldcupstandings.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * DTO to connect match result with correct typings
 */
@Getter
public class CorrectTypingOutput {
    @JsonProperty("homeTeam")
    private String homeTeam;
    @JsonProperty("result")
    private String result;
    @JsonProperty("awayTeam")
    private String awayTeam;
    @JsonProperty("correctTypings")
    private Integer correctTypings;

    public CorrectTypingOutput(String homeTeam, String result, String awayTeam, Integer correctTypings) {
        this.homeTeam = homeTeam;
        this.result = result;
        this.awayTeam = awayTeam;
        this.correctTypings = correctTypings;
    }
}
