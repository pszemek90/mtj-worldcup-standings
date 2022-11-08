package com.pszemek.mtjworldcupstandings.service;

import com.pszemek.mtjworldcupstandings.dto.CorrectTypingOutput;
import com.pszemek.mtjworldcupstandings.dto.FootballMatchOutput;
import com.pszemek.mtjworldcupstandings.entity.MatchTyping;
import com.pszemek.mtjworldcupstandings.enums.TypingResultEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ResultsService {

    private static final Logger logger = LoggerFactory.getLogger(ResultsService.class);

    private final MatchesService matchesService;
    private final TypingsService typingsService;

    public ResultsService(MatchesService matchesService, TypingsService typingsService) {
        this.matchesService = matchesService;
        this.typingsService = typingsService;
    }

    public Map<String, List<CorrectTypingOutput>> getAllResults() {
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
        Map<String, List<CorrectTypingOutput>> finishedMatchesByDateString = new TreeMap<>(Comparator.reverseOrder());
        List<MatchTyping> correctTypings = typingsService.getAllTypingEntities().stream()
                .filter(typing -> typing.getStatus() == TypingResultEnum.CORRECT)
                .collect(Collectors.toList());
        finishedMatchesByDate.forEach((k, v) -> finishedMatchesByDateString.put(k.toLocalDate().toString(), connectMatchWithTypings(v, correctTypings)));
        logger.info("Finished matches mapped to string: {}", finishedMatchesByDateString.size());
        return finishedMatchesByDateString;
    }

    private List<CorrectTypingOutput> connectMatchWithTypings(List<FootballMatchOutput> matches, List<MatchTyping> correctTypings) {
        List<CorrectTypingOutput> correctTypingOutputs = new ArrayList<>();
        for(FootballMatchOutput match : matches) {
            int matchedCorrectTypings = (int) correctTypings.stream()
                    .filter(correctTyping -> correctTyping.getMatchId().equals(match.getId()))
                    .count();
            String result = String.format("%d - %d", match.getHomeScore(), match.getAwayScore());
            correctTypingOutputs.add(new CorrectTypingOutput(match.getHomeTeam(), result, match.getAwayTeam(), matchedCorrectTypings));
        }
        return correctTypingOutputs;
    }
}
