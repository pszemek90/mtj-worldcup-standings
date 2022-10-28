package com.pszemek.mtjworldcupstandings.mapper;

import com.pszemek.mtjworldcupstandings.dto.FootballMatchOutput;
import com.pszemek.mtjworldcupstandings.entity.Match;

import java.util.List;
import java.util.stream.Collectors;

public class MatchOutputEntityMapper {
    public static Match mapFromOutput(FootballMatchOutput output){
        return new Match()
                .setMatchId(output.getId())
                .setAwayScore(output.getAwayScore())
                .setAwayTeam(output.getAwayTeam())
                .setHomeScore(output.getHomeScore())
                .setHomeTeam(output.getHomeTeam())
                .setDate(output.getDate())
                .setFinished(output.isFinished());
    }

    public static FootballMatchOutput mapFromEntity(Match entity) {
        return new FootballMatchOutput()
                .setId(entity.getMatchId())
                .setAwayScore(entity.getAwayScore())
                .setAwayTeam(entity.getAwayTeam())
                .setHomeScore(entity.getHomeScore())
                .setHomeTeam(entity.getHomeTeam())
                .setDate(entity.getDate())
                .setFinished(entity.getFinished());
    }

    public static List<Match> mapFromOutput(List<FootballMatchOutput> output){
        return output.stream().map(MatchOutputEntityMapper::mapFromOutput).collect(Collectors.toList());
    }

    public static List<FootballMatchOutput> mapFromEntity(List<Match> entities) {
        return entities.stream().map(MatchOutputEntityMapper::mapFromEntity).collect(Collectors.toList());
    }
}
