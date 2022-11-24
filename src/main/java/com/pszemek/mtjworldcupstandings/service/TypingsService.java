package com.pszemek.mtjworldcupstandings.service;

import com.pszemek.mtjworldcupstandings.dto.*;
import com.pszemek.mtjworldcupstandings.entity.Match;
import com.pszemek.mtjworldcupstandings.entity.MatchTyping;
import com.pszemek.mtjworldcupstandings.entity.User;
import com.pszemek.mtjworldcupstandings.mapper.MatchTypingFootballMatchOutputMapper;
import com.pszemek.mtjworldcupstandings.repository.MatchTypingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.pszemek.mtjworldcupstandings.enums.TypingResultEnum.CORRECT;

@Service
public class TypingsService {
    private static final Logger logger = LoggerFactory.getLogger(TypingsService.class);

    private final MatchTypingRepository typingRepository;
    private final UserService userService;
    private final MatchesService matchesService;

    public TypingsService(MatchTypingRepository typingRepository, UserService userService, MatchesService matchesService) {
        this.typingRepository = typingRepository;
        this.userService = userService;
        this.matchesService = matchesService;
    }

    public Map<String, List<TypingOutput>> getTypingsForUser(Long userId) {
        logger.info("Getting typings for user: {}", userId);
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
        logger.info("Getting all typing entities");
        return typingRepository.findAll();
    }

    public void saveAll(List<MatchTyping> allTypings) {
        logger.info("Saving typing list");
        typingRepository.saveAll(allTypings);
    }

    public List<TyperScore> getAllTyperScores() {
        logger.info("Getting all typer scores");
        Map<Long, List<MatchTyping>> typingsByUserId = typingRepository.findAll().stream().collect(Collectors.groupingBy(MatchTyping::getUserId));
        Map<Long, Integer> typerScoreMap = new TreeMap<>();
        for (Map.Entry<Long, List<MatchTyping>> entry : typingsByUserId.entrySet()) {
            List<MatchTyping> correctTypings = entry.getValue().stream().filter(typing -> typing.getStatus() == CORRECT).collect(Collectors.toList());
            typerScoreMap.put(entry.getKey(), correctTypings.size());
        }
        List<TyperScore> typerScoreList = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : typerScoreMap.entrySet()) {
            User user = userService.getByUserId(entry.getKey());
            TyperScore typerScore = new TyperScore()
                    .setUsername(user.getUsername())
                    .setCorrectTypings(entry.getValue())
                    .setBalance(user.getBalance())
                    .setCountry(user.getCountry());
            typerScoreList.add(typerScore);
        }
        return typerScoreList.stream().sorted((t1, t2) -> t2.getCorrectTypings().compareTo(t1.getCorrectTypings())).collect(Collectors.toList());
    }

    public Map<LocalDate, MatchTypings> getAllUsersTypings() {
        Map<LocalDate, MatchTypings> usersTypings = new TreeMap<>(Comparator.reverseOrder());
        List<MatchTyping> allTypings = getAllTypingEntities();
        List<Match> finishedMatches = matchesService.getAllFinishedMatches();
        List<Integer> finishedMatchIds = finishedMatches.stream().map(Match::getMatchId).collect(Collectors.toList());
        // typings mapped by match date
        Map<LocalDate, List<MatchTyping>> typingsByDate = allTypings.stream()
                .filter(typing -> finishedMatchIds.contains(typing.getMatchId()))
                .collect(Collectors.groupingBy(MatchTyping::getMatchDate));
        //for every date
        for (Map.Entry<LocalDate, List<MatchTyping>> entry : typingsByDate.entrySet()) {
            LocalDate entryDate = entry.getKey();
            Map<Integer, List<MatchTyping>> typingsByMatch = entry.getValue().stream().collect(Collectors.groupingBy(MatchTyping::getMatchId));
            MatchTypings matchTyping = new MatchTypings();
            //for every match in that date
            for (Map.Entry<Integer, List<MatchTyping>> matchEntry : typingsByMatch.entrySet()) {
                List<UserTyping> userMatchTypings = new ArrayList<>();
                String match = null;
                //for every typing in that match
                for (MatchTyping userTypings : matchEntry.getValue()) {
                    if(match == null) {
                        match = String.format("%s - %s", userTypings.getHomeTeam(), userTypings.getAwayTeam());
                    }
                    UserTyping userTyping = new UserTyping()
                            .setResult(String.format("%d - %d", userTypings.getHomeScore(), userTypings.getAwayScore()))
                            .setUsername(userService.getByUserId(userTypings.getUserId()).getUsername())
                            .setCorrect(userTypings.getStatus() == CORRECT);
                    userMatchTypings.add(userTyping);
                }
                //put into map pair match - list of typings
                userMatchTypings.sort(Comparator.comparing(UserTyping::getUsername));
                matchTyping.getMatchTypings().put(match, userMatchTypings);
            }
            usersTypings.put(entryDate, matchTyping);
        }
        return usersTypings;
    }
}
