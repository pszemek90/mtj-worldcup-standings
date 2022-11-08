package com.pszemek.mtjworldcupstandings.service;

import com.pszemek.mtjworldcupstandings.dto.FootballMatchOutput;
import com.pszemek.mtjworldcupstandings.dto.TyperScore;
import com.pszemek.mtjworldcupstandings.dto.TypingOutput;
import com.pszemek.mtjworldcupstandings.entity.MatchTyping;
import com.pszemek.mtjworldcupstandings.entity.User;
import com.pszemek.mtjworldcupstandings.enums.TypingResultEnum;
import com.pszemek.mtjworldcupstandings.mapper.MatchTypingFootballMatchOutputMapper;
import com.pszemek.mtjworldcupstandings.repository.MatchTypingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TypingsService {
    private final MatchTypingRepository typingRepository;
    private final UserService userService;

    public TypingsService(MatchTypingRepository typingRepository, UserService userService) {
        this.typingRepository = typingRepository;
        this.userService = userService;
    }

    public Map<String, List<TypingOutput>> getTypingsForUser(Long userId) {
        List<MatchTyping> userTypings = typingRepository.findByUserId(userId);
        List<FootballMatchOutput> typingsOutput = MatchTypingFootballMatchOutputMapper.mapToFootballMatchOutput(userTypings);
        Map<LocalDateTime, List<FootballMatchOutput>> userTypingsMap = typingsOutput.stream().collect(Collectors.groupingBy(FootballMatchOutput::getDate));
        var userTypingsWithStringDateMap = new TreeMap<String, List<TypingOutput>>(Comparator.reverseOrder());
        userTypingsMap.forEach((k, v) -> userTypingsWithStringDateMap.put(k.toLocalDate().toString(), mapToTypingOutput(v)));
        return userTypingsWithStringDateMap;
    }

    private List<TypingOutput> mapToTypingOutput(List<FootballMatchOutput> matches) {
        List<TypingOutput> typingOutputs = new ArrayList<>();
        for (FootballMatchOutput match : matches) {
            typingOutputs.add(new TypingOutput(
                    match.getHomeTeam(),
                    String.format("%d - %d", match.getHomeScore(), match.getAwayScore()),
                    match.getAwayTeam(),
                    match.getStatus().toString()
            ));
        }
        return typingOutputs;
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

    public List<TyperScore> getAllTyperScores() {
        Map<Long, List<MatchTyping>> typingsByUserId = typingRepository.findAll().stream().collect(Collectors.groupingBy(MatchTyping::getUserId));
        Map<Long, Integer> typerScoreMap = new TreeMap<>();
        for (Map.Entry<Long, List<MatchTyping>> entry : typingsByUserId.entrySet()) {
            List<MatchTyping> correctTypings = entry.getValue().stream().filter(typing -> typing.getStatus() == TypingResultEnum.CORRECT).collect(Collectors.toList());
            typerScoreMap.put(entry.getKey(), correctTypings.size());
        }
        List<TyperScore> typerScoreList = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : typerScoreMap.entrySet()) {
            User user = userService.getByUserId(entry.getKey());
            TyperScore typerScore = new TyperScore()
                    .setUsername(user.getUsername())
                    .setCorrectTypings(entry.getValue());
            typerScoreList.add(typerScore);
        }
        return typerScoreList.stream().sorted((t1, t2) -> t2.getCorrectTypings().compareTo(t1.getCorrectTypings())).collect(Collectors.toList());
    }
}
