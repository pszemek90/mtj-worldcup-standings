package com.pszemek.mtjworldcupstandings.controller;

import com.pszemek.mtjworldcupstandings.dto.*;
import com.pszemek.mtjworldcupstandings.service.TypingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/typings")
public class TypingController {

    private static final Logger logger = LoggerFactory.getLogger(TypingController.class);

    private final TypingsService typingsService;

    public TypingController(TypingsService typingsService) {
        this.typingsService = typingsService;
    }

    @GetMapping()
    public Map<String, List<TypingOutput>> getTypingsForUser(Long userId, Authentication authentication) {
        UserDto user = (UserDto) authentication.getPrincipal();
        if(!userId.equals(user.getId())){
            logger.error("Attempt to unauthorized access to user typings by user: {}", user.getId());
            throw new HttpServerErrorException(HttpStatus.UNAUTHORIZED, "Unauthorized access to user's typings");
        }
        return typingsService.getTypingsForUser(userId);
    }

    @GetMapping("/typerScores")
    public List<TyperScore> getAllTyperScores() {
        return typingsService.getAllTyperScores();
    }

    @GetMapping("/allTypings")
    public Map<LocalDate, MatchTypings> getAllTypings() {
        return typingsService.getAllUsersTypings();
    }
}
