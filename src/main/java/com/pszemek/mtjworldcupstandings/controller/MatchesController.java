package com.pszemek.mtjworldcupstandings.controller;

import com.pszemek.mtjworldcupstandings.dto.FootballMatchOutput;
import com.pszemek.mtjworldcupstandings.dto.InputTypings;
import com.pszemek.mtjworldcupstandings.dto.UserDto;
import com.pszemek.mtjworldcupstandings.service.MatchesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/matches")
public class MatchesController {

    private final Logger logger = LoggerFactory.getLogger(MatchesController.class);

    private final MatchesService matchesService;

    public MatchesController(MatchesService matchesService) {
        this.matchesService = matchesService;
    }

    @GetMapping()
    public List<FootballMatchOutput> getAllMatches() {
        return matchesService.getAllMatches();
    }

    @GetMapping("/today")
    public List<FootballMatchOutput> getTodayMatches(String date) {
        return matchesService.getMatchesForToday(date);
    }

    @PostMapping("/typings")
    public void sendTypings(@RequestBody InputTypings inputTypings, Authentication authentication) {
        UserDto user = (UserDto) authentication.getPrincipal();
        Long userId = inputTypings.getUserId();
        if(!userId.equals(user.getId())){
            logger.error("Attempt to unauthorized typings send by user: {}", user.getId());
            throw new HttpServerErrorException(HttpStatus.UNAUTHORIZED, "Unauthorized access to user's typings send");
        }
        logger.info("Saving typings for userId: {}", inputTypings.getUserId());
        matchesService.saveTypings(inputTypings);
    }
}
