package com.pszemek.mtjworldcupstandings.mapper;

import com.pszemek.mtjworldcupstandings.dto.FootballMatchInput;
import com.pszemek.mtjworldcupstandings.dto.FootballMatchOutput;

import java.util.List;
import java.util.stream.Collectors;

public class FootballMatchMapper {
    public static FootballMatchOutput mapFromInput(FootballMatchInput input){
        return new FootballMatchOutput()
                .setId(input.getId())
                .setAwayScore(input.getAwayScore())
                .setHomeScore(input.getHomeScore())
                .setAwayTeam(input.getAwayTeamEn())
                .setHomeTeam(input.getHomeTeamEn())
                .setDate(input.getDate())
                .setFinished(input.getFinished());
    }

    public static List<FootballMatchOutput> mapFromInput(List<FootballMatchInput> input){
        return input.stream().map(FootballMatchMapper::mapFromInput).collect(Collectors.toList());
    }
}
