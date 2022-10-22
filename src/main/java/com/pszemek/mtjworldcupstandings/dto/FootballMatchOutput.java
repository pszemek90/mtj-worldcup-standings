package com.pszemek.mtjworldcupstandings.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class FootballMatchOutput {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("awayScore")
    private Integer awayScore;
    @JsonProperty("homeScore")
    private Integer homeScore;
    @JsonProperty("awayTeam")
    private String awayTeam;
    @JsonProperty("homeTeam")
    private String homeTeam;
    @JsonProperty("date")
    private LocalDateTime date;

    public FootballMatchOutput setId(Integer id) {
        this.id = id;
        return this;
    }

    public FootballMatchOutput setAwayScore(Integer awayScore) {
        this.awayScore = awayScore;
        return this;
    }

    public FootballMatchOutput setHomeScore(Integer homeScore) {
        this.homeScore = homeScore;
        return this;
    }

    public FootballMatchOutput setAwayTeam(String awayTeam) {
        this.awayTeam = awayTeam;
        return this;
    }

    public FootballMatchOutput setHomeTeam(String homeTeam) {
        this.homeTeam = homeTeam;
        return this;
    }

    public FootballMatchOutput setDate(LocalDateTime date) {
        this.date = date;
        return this;
    }
}
