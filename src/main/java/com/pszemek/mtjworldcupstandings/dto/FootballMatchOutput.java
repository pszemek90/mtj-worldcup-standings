package com.pszemek.mtjworldcupstandings.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pszemek.mtjworldcupstandings.enums.TypingResultEnum;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

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
    @JsonProperty("finished")
    private Boolean finished;
    @JsonProperty("status")
    private TypingResultEnum status;
    @JsonProperty("pool")
    private BigDecimal pool;

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

    public Boolean isFinished() {
        return this.finished;
    }

    public FootballMatchOutput setStatus(TypingResultEnum status) {
        this.status = status;
        return this;
    }

    public FootballMatchOutput setPool(BigDecimal pool) {
        this.pool = pool;
        return this;
    }

    //todo for test purposes, probably to delete
    public FootballMatchOutput setFinished(Boolean finished) {
        this.finished = finished;
        return this;
    }

    @Override
    public String toString() {
        return "FootballMatchOutput{" +
                "id=" + id +
                ", awayScore=" + awayScore +
                ", homeScore=" + homeScore +
                ", awayTeam='" + awayTeam + '\'' +
                ", homeTeam='" + homeTeam + '\'' +
                ", date=" + date +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FootballMatchOutput that = (FootballMatchOutput) o;
        return Objects.equals(id, that.id)
                && Objects.equals(awayScore, that.awayScore)
                && Objects.equals(homeScore, that.homeScore)
                && Objects.equals(awayTeam, that.awayTeam)
                && Objects.equals(homeTeam, that.homeTeam)
                && Objects.equals(date, that.date)
                && Objects.equals(finished, that.finished);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, awayScore, homeScore, awayTeam, homeTeam, date, finished);
    }

    public FootballMatchOutput normalizeMatchTime() {
        return this.setDate(LocalDateTime.of(this.date.toLocalDate(), LocalTime.of(8, 0)));
    }
}
