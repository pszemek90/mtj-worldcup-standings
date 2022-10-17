package com.pszemek.mtjworldcupstandings.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class FootballMatchInput {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("away_score")
    private Integer awayScore;
    @JsonProperty("home_score")
    private Integer homeScore;
    @JsonProperty("away_team_en")
    private String awayTeamEn;
    @JsonProperty("home_team_en")
    private String homeTeamEn;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "M/d/yyyy HH:mm")
    @JsonProperty("local_date")
    private LocalDateTime date;

}
