package com.pszemek.mtjworldcupstandings.entity;

import lombok.Getter;

import javax.persistence.*;


@Getter
@Entity(name = "match_typing")
public class MatchTyping {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "home_score")
    private Integer homeScore;
    @Column(name = "away_score")
    private Integer awayScore;
    @Column(name = "match_id")
    private Integer matchId;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "is_correct")
    private Boolean isCorrect;

    public MatchTyping setHomeScore(Integer homeScore) {
        this.homeScore = homeScore;
        return this;
    }

    public MatchTyping setAwayScore(Integer awayScore) {
        this.awayScore = awayScore;
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
}
