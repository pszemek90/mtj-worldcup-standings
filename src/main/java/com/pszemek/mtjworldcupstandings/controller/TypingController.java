package com.pszemek.mtjworldcupstandings.controller;

import com.pszemek.mtjworldcupstandings.dto.FootballMatchOutput;
import com.pszemek.mtjworldcupstandings.entity.MatchTyping;
import com.pszemek.mtjworldcupstandings.mapper.MatchTypingFootballMatchOutputMapper;
import com.pszemek.mtjworldcupstandings.repository.MatchTypingRepository;
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
@RequestMapping("/typings")
public class TypingController {

    private final MatchTypingRepository typingRepository;

    public TypingController(MatchTypingRepository typingRepository) {
        this.typingRepository = typingRepository;
    }

    @GetMapping()
    public Map<String, List<FootballMatchOutput>> getTypingsForUser(Long userId) {
        List<MatchTyping> userTypings = typingRepository.findByUserId(userId);
        List<FootballMatchOutput> typingsOutput = MatchTypingFootballMatchOutputMapper.mapToFootballMatchOutput(userTypings);
        Map<LocalDateTime, List<FootballMatchOutput>> userTypingsMap = typingsOutput.stream().collect(Collectors.groupingBy(FootballMatchOutput::getDate));
        var userTypingsWithStringDateMap = new HashMap<String, List<FootballMatchOutput>>();
        userTypingsMap.forEach((k, v) -> userTypingsWithStringDateMap.put(k.toLocalDate().toString(), v));
        return userTypingsWithStringDateMap;
    }
}
