package com.pszemek.mtjworldcupstandings.controller;

import com.pszemek.mtjworldcupstandings.dto.FootballMatchOutput;
import com.pszemek.mtjworldcupstandings.dto.Typings;
import com.pszemek.mtjworldcupstandings.service.MatchesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class MainController {

    private final Logger logger = LoggerFactory.getLogger(MainController.class);

    private final MatchesService matchesService;

    public MainController(MatchesService matchesService) {
        this.matchesService = matchesService;
    }

    @GetMapping("/matches")
    public List<FootballMatchOutput> getAllMatches() {
        return matchesService.getAllMatches();
    }

    @GetMapping("/matches/today")
    public List<FootballMatchOutput> getTodayMatches(String date) {
        return matchesService.getMatchesForToday(date);
    }

    @PostMapping("/matches/typings")
    public void sendTypings(@RequestBody Typings typings) {
        logger.info("Saving typings for userId: {}", typings.getUserId());
        matchesService.saveTypings(typings);
    }
}
