package com.pszemek.mtjworldcupstandings.service;

import com.pszemek.mtjworldcupstandings.dto.FootballMatchOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class ResultsService {

    private static final Logger logger = LoggerFactory.getLogger(ResultsService.class);

    private final MatchesService matchesService;

    public ResultsService(MatchesService matchesService) {
        this.matchesService = matchesService;
    }

    public Map<String, List<FootballMatchOutput>> getAllResults() {
        logger.info("Getting all matches results");
        List<FootballMatchOutput> allMatches = matchesService.getAllMatches();
        logger.info("Matches fetched from db: {}", allMatches.size());
        // todo test finished match, delete before prod move
        /*FootballMatchOutput testMatch = new FootballMatchOutput()
                .setId(999)
                .setDate(LocalDateTime.now())
                .setHomeTeam("testTeam1")
                .setAwayTeam("testTeam2")
                .setHomeScore(2)
                .setAwayScore(0)
                .setFinished(true);
        allMatches.add(testMatch);*/
        Map<LocalDateTime, List<FootballMatchOutput>> finishedMatchesByDate =
                allMatches.stream()
                        .filter(FootballMatchOutput::isFinished)
                        .map(FootballMatchOutput::normalizeMatchTime)
                        .collect(Collectors.groupingBy(FootballMatchOutput::getDate));
        logger.info("Finished matches from db: {}", finishedMatchesByDate.size());
        Map<String, List<FootballMatchOutput>> finishedMatchesByDateString = new TreeMap<>(Comparator.reverseOrder());
        finishedMatchesByDate.forEach((k, v) -> finishedMatchesByDateString.put(k.toLocalDate().toString(), v));
        logger.info("Finished matches mapped to string: {}", finishedMatchesByDateString.size());
        return finishedMatchesByDateString;
    }
}
