package com.pszemek.mtjworldcupstandings.entity;

import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "match")
@Getter
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "match_id")
    private Integer matchId;
    @Column(name = "home_team")
    private String homeTeam;
    @Column(name = "away_team")
    private String awayTeam;
    @Column(name = "home_score")
    private Integer homeScore;
    @Column(name = "away_score")
    private Integer awayScore;
    @Column(name = "date")
    private LocalDateTime date;
    @Column(name = "finished")
    private Boolean finished;

    public Match setMatchId(Integer matchId) {
        this.matchId = matchId;
        return this;
    }

    public Match setHomeTeam(String homeTeam) {
        this.homeTeam = homeTeam;
        return this;
    }

    public Match setAwayTeam(String awayTeam) {
        this.awayTeam = awayTeam;
        return this;
    }

    public Match setHomeScore(Integer homeScore) {
        this.homeScore = homeScore;
        return this;
    }

    public Match setAwayScore(Integer awayScore) {
        this.awayScore = awayScore;
        return this;
    }

    public Match setDate(LocalDateTime date) {
        this.date = date;
        return this;
    }

    public Match setFinished(Boolean finished) {
        this.finished = finished;
        return this;
    }
}
