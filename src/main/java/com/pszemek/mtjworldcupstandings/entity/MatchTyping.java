package com.pszemek.mtjworldcupstandings.entity;

import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDate;


@Getter
@Entity(name = "match_typing")
public class MatchTyping {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "home_score")
    private Integer homeScore;
    @Column(name = "home_team")
    private String homeTeam;
    @Column(name = "away_score")
    private Integer awayScore;
    @Column(name = "away_team")
    private String awayTeam;
    @Column(name = "match_id")
    private Integer matchId;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "is_correct")
    private Boolean isCorrect;
    @Column(name = "match_date")
    private LocalDate matchDate;

    public MatchTyping setHomeScore(Integer homeScore) {
        this.homeScore = homeScore;
        return this;
    }

    public MatchTyping setHomeTeam(String homeTeam) {
        this.homeTeam = homeTeam;
        return this;
    }

    public MatchTyping setAwayScore(Integer awayScore) {
        this.awayScore = awayScore;
        return this;
    }

    public MatchTyping setAwayTeam(String awayTeam) {
        this.awayTeam = awayTeam;
        return this;
    }

    public MatchTyping setMatchId(Integer matchId) {
        this.matchId = matchId;
        return this;
    }

    public MatchTyping setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public MatchTyping setCorrect(Boolean correct) {
        isCorrect = correct;
        return this;
    }

    public MatchTyping setMatchDate(LocalDate matchDate) {
        this.matchDate = matchDate;
        return this;
    }
}
