package com.pszemek.mtjworldcupstandings.mapper;

import com.pszemek.mtjworldcupstandings.dto.FootballMatchOutput;
import com.pszemek.mtjworldcupstandings.entity.MatchTyping;

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
                .setDate(LocalDateTime.of(entity.getMatchDate(), LocalTime.of(8, 0)) );
    }

    public static List<FootballMatchOutput> mapToFootballMatchOutput(List<MatchTyping> entities) {
        return entities.stream()
                .map(MatchTypingFootballMatchOutputMapper::mapToFootballMatchOutput)
                .collect(Collectors.toList());
    }
}
