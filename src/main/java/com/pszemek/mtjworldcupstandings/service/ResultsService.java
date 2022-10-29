package com.pszemek.mtjworldcupstandings.service;

import com.pszemek.mtjworldcupstandings.dto.FootballMatchOutput;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ResultsService {

    private final MatchesService matchesService;

    public ResultsService(MatchesService matchesService) {
        this.matchesService = matchesService;
    }

    public Map<String, List<FootballMatchOutput>> getAllResults() {
        List<FootballMatchOutput> allMatches = matchesService.getAllMatches();
        // todo test finished match, delete before prod move
        FootballMatchOutput testMatch = new FootballMatchOutput()
                .setId(999)
                .setDate(LocalDateTime.now())
                .setHomeTeam("testTeam1")
                .setAwayTeam("testTeam2")
                .setHomeScore(2)
                .setAwayScore(0)
                .setFinished(true);
        allMatches.add(testMatch);
        Map<LocalDateTime, List<FootballMatchOutput>> finishedMatchesByDate =
                allMatches.stream()
                        .filter(FootballMatchOutput::isFinished)
                        .collect(Collectors.groupingBy(FootballMatchOutput::getDate));
        Map<String, List<FootballMatchOutput>> finishedMatchesByDateString = new TreeMap<>(Comparator.reverseOrder());
        finishedMatchesByDate.forEach((k, v) -> finishedMatchesByDateString.put(k.toLocalDate().toString(), v));

        return finishedMatchesByDateString;
    }
}
