package com.pszemek.mtjworldcupstandings.mapper;

import com.pszemek.mtjworldcupstandings.dto.FootballMatchInput;
import com.pszemek.mtjworldcupstandings.dto.FootballMatchOutput;

import java.util.List;
import java.util.stream.Collectors;

public class MatchInputOutputMapper {
    public static FootballMatchOutput mapFromInput(FootballMatchInput input){
        return new FootballMatchOutput()
                .setId(input.getId())
                .setAwayScore(input.getAwayScore())
                .setHomeScore(input.getHomeScore())
                .setAwayTeam(TeamNameMapper.mapTeam(input.getAwayTeamEn()))
                .setHomeTeam(TeamNameMapper.mapTeam(input.getHomeTeamEn()))
                //adjusting to Poland time(CET) from time fetched from api
                .setDate(input.getDate().minusHours(2L))
                .setFinished(input.getFinished());
    }

    public static List<FootballMatchOutput> mapFromInput(List<FootballMatchInput> input){
        return input.stream().map(MatchInputOutputMapper::mapFromInput).collect(Collectors.toList());
    }
}
