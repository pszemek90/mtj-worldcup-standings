package com.pszemek.mtjworldcupstandings.controller;

import com.pszemek.mtjworldcupstandings.dto.FootballMatchOutput;
import com.pszemek.mtjworldcupstandings.dto.TyperScore;
import com.pszemek.mtjworldcupstandings.dto.TypingOutput;
import com.pszemek.mtjworldcupstandings.service.TypingsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/typings")
public class TypingController {

    private final TypingsService typingsService;

    public TypingController(TypingsService typingsService) {
        this.typingsService = typingsService;
    }

    @GetMapping()
    public Map<String, List<TypingOutput>> getTypingsForUser(Long userId) {
        return typingsService.getTypingsForUser(userId);
    }

    @GetMapping("/typerScores")
    public List<TyperScore> getAllTyperScores() {
        return typingsService.getAllTyperScores();
    }
}
