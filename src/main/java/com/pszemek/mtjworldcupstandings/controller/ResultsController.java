package com.pszemek.mtjworldcupstandings.controller;

import com.pszemek.mtjworldcupstandings.dto.FootballMatchOutput;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/results")
public class ResultsController {

    private final MainController mainController;

    public ResultsController(MainController mainController) {
        this.mainController = mainController;
    }

    @GetMapping()
    public Map<String, List<FootballMatchOutput>> getAllResults() {
        List<FootballMatchOutput> allMatches = mainController.getAllMatches();
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
        List<FootballMatchOutput> testList = List.of(testMatch);
        Map<LocalDateTime, List<FootballMatchOutput>> finishedMatchesByDate =
                testList.stream()
                .filter(FootballMatchOutput::isFinished)
                .collect(Collectors.groupingBy(FootballMatchOutput::getDate));
        Map<String, List<FootballMatchOutput>> finishedMatchesByDateString = new HashMap<>();
        finishedMatchesByDate.forEach((k, v) -> finishedMatchesByDateString.put(k.toLocalDate().toString(), v));
        return finishedMatchesByDateString;
    }

}
