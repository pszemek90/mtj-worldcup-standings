package com.pszemek.mtjworldcupstandings.service;

import com.pszemek.mtjworldcupstandings.dto.FootballMatchOutput;
import com.pszemek.mtjworldcupstandings.entity.MatchTyping;
import com.pszemek.mtjworldcupstandings.mapper.MatchTypingFootballMatchOutputMapper;
import com.pszemek.mtjworldcupstandings.repository.MatchTypingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TypingsService {
    private final MatchTypingRepository typingRepository;

    public TypingsService(MatchTypingRepository typingRepository) {
        this.typingRepository = typingRepository;
    }

    public Map<String, List<FootballMatchOutput>> getTypingsForUser(Long userId) {
        List<MatchTyping> userTypings = typingRepository.findByUserId(userId);
        List<FootballMatchOutput> typingsOutput = MatchTypingFootballMatchOutputMapper.mapToFootballMatchOutput(userTypings);
        Map<LocalDateTime, List<FootballMatchOutput>> userTypingsMap = typingsOutput.stream().collect(Collectors.groupingBy(FootballMatchOutput::getDate));
        var userTypingsWithStringDateMap = new TreeMap<String, List<FootballMatchOutput>>(Comparator.reverseOrder());
        userTypingsMap.forEach((k, v) -> userTypingsWithStringDateMap.put(k.toLocalDate().toString(), v));
        return userTypingsWithStringDateMap;
    }

    public List<FootballMatchOutput> getAllTypings() {
        List<MatchTyping> allTypingEntities = typingRepository.findAll();
        return MatchTypingFootballMatchOutputMapper.mapToFootballMatchOutput(allTypingEntities);
    }

    public List<MatchTyping> getAllTypingEntities() {
        return typingRepository.findAll();
    }

    public void saveAll(List<MatchTyping> allTypings) {
        typingRepository.saveAll(allTypings);
    }
}
