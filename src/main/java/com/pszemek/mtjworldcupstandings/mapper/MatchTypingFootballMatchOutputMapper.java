package com.pszemek.mtjworldcupstandings.mapper;

import com.pszemek.mtjworldcupstandings.dto.FootballMatchOutput;
import com.pszemek.mtjworldcupstandings.entity.MatchTyping;
import com.pszemek.mtjworldcupstandings.enums.TypingResultEnum;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

public class MatchTypingFootballMatchOutputMapper {

    public static FootballMatchOutput mapToFootballMatchOutput(MatchTyping entity) {
        return new FootballMatchOutput()
                .setAwayScore(entity.getAwayScore())
                .setHomeScore(entity.getHomeScore())
                .setAwayTeam(entity.getAwayTeam())
                .setHomeTeam(entity.getHomeTeam())
                .setDate(LocalDateTime.of(entity.getMatchDate(), LocalTime.of(8, 0)) )
                .setStatus(entity.getStatus());
    }

    public static MatchTyping mapToEntity(FootballMatchOutput output) {
        return new MatchTyping()
                .setAwayScore(output.getAwayScore())
                .setAwayTeam(output.getAwayTeam())
                .setHomeScore(output.getHomeScore())
                .setHomeTeam(output.getHomeTeam())
                .setMatchId(output.getId())
                .setMatchDate(output.getDate().toLocalDate());
    }

    public static List<FootballMatchOutput> mapToFootballMatchOutput(List<MatchTyping> entities) {
        return entities.stream()
                .map(MatchTypingFootballMatchOutputMapper::mapToFootballMatchOutput)
                .collect(Collectors.toList());
    }
}
