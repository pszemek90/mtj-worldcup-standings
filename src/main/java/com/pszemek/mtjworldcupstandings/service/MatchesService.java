package com.pszemek.mtjworldcupstandings.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pszemek.mtjworldcupstandings.configuration.CurrentBearerToken;
import com.pszemek.mtjworldcupstandings.dto.*;
import com.pszemek.mtjworldcupstandings.entity.Match;
import com.pszemek.mtjworldcupstandings.entity.MatchTyping;
import com.pszemek.mtjworldcupstandings.mapper.MatchInputOutputMapper;
import com.pszemek.mtjworldcupstandings.mapper.MatchOutputEntityMapper;
import com.pszemek.mtjworldcupstandings.repository.MatchRepository;
import com.pszemek.mtjworldcupstandings.repository.MatchTypingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MatchesService {

    private final Logger logger = LoggerFactory.getLogger(MatchesService.class);

    @Value("${api.all.matches.url}")
    private String allMatchesUrl;

    @Value("${api.login.url}")
    private String loginUrl;

    @Value("${fallback.json.path}")
    private String fallbackJsonPath;

    private final RestTemplate restTemplate;
    private final ApiLoginRequest apiLoginRequest;
    private final MatchTypingRepository matchTypingRepository;
    private final MatchRepository matchRepository;

    public MatchesService(RestTemplate restTemplate, ApiLoginRequest apiLoginRequest, MatchTypingRepository matchTypingRepository, MatchRepository matchRepository) {
        this.restTemplate = restTemplate;
        this.apiLoginRequest = apiLoginRequest;
        this.matchTypingRepository = matchTypingRepository;
        this.matchRepository = matchRepository;
    }

    public List<FootballMatchOutput> getMatchesForToday(String stringDate) {
        logger.info("Getting matches for {}", stringDate);
        LocalDate date = LocalDate.parse(stringDate);
        List<FootballMatchOutput> todaysMatches = getAllMatches().stream()
                .filter(match -> match.getDate().toLocalDate().equals(date))
                .collect(Collectors.toList());
        //todo just for testing in dev, delete before prod move
        if(date.isEqual(LocalDate.now())) {
            todaysMatches.add(new FootballMatchOutput()
                    .setDate(LocalDateTime.of(LocalDate.now(), LocalTime.of(20, 0)))
                    .setId(999)
                    .setAwayScore(0)
                    .setHomeScore(0)
                    .setAwayTeam("testTeam1")
                    .setHomeTeam("testTeam2"));
        }
        return todaysMatches;
    }

    public List<FootballMatchOutput> getAllMatches() {
        List<Match> matchesFromDb = matchRepository.findAll();
        return MatchOutputEntityMapper.mapFromEntity(matchesFromDb);
        }

    private void getNewBearerToken() {
        HttpEntity<ApiLoginRequest> request = new HttpEntity<>(apiLoginRequest);
        ResponseEntity<BearerTokenDto> bearerTokenResponseEntity;
        try {
            bearerTokenResponseEntity = restTemplate.postForEntity(loginUrl, request, BearerTokenDto.class);
        } catch (Exception ex) {
            logger.error("Exception while making rest call to api: {}", ex.getMessage());
            throw new HttpClientErrorException(HttpStatus.BAD_GATEWAY ,"Server error while getting new Bearer token");
        }
        BearerTokenDto responseBody = bearerTokenResponseEntity.getBody();
        if (responseBody != null && responseBody.getToken() != null) {
            logger.info("Setting new Bearer token");
            CurrentBearerToken.setToken(responseBody.getToken());
        } else {
            logger.error("Something went wrong with login response. Returned body: {} ", responseBody);
        }
    }

    private List<FootballMatchOutput> getMatchesFromPlainJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            MatchesInputResponse plainJsonResponse = objectMapper.readValue(new File(fallbackJsonPath), MatchesInputResponse.class);
            List<FootballMatchInput> matchInputs = plainJsonResponse.getMatches();
            if(matchInputs != null){
                return MatchInputOutputMapper.mapFromInput(matchInputs);
            }
        } catch (IOException e) {
            logger.error("Couldn't read from plain JSON. Reason: {}", e.getMessage());
        }
        return List.of();
    }

    public void saveTypings(Typings typings) {
        List<FootballMatchOutput> matches = typings.getMatches();
        logger.info("Typed matches amount: {}", matches.size());
        Long userId = typings.getUserId();
        for(FootballMatchOutput match : matches) {
            Optional<MatchTyping> matchAlreadyTyped = matchTypingRepository.findByUserIdAndMatchId(userId, match.getId());
            if(matchAlreadyTyped.isPresent()){
                MatchTyping matchToUpdate = matchAlreadyTyped.get()
                        .setAwayScore(match.getAwayScore())
                        .setHomeScore(match.getHomeScore());
                matchTypingRepository.save(matchToUpdate);
            } else {
                MatchTyping typing = new MatchTyping()
                        .setUserId(userId)
                        .setAwayScore(match.getAwayScore())
                        .setAwayTeam(match.getAwayTeam())
                        .setHomeScore(match.getHomeScore())
                        .setHomeTeam(match.getHomeTeam())
                        .setMatchId(match.getId())
                        .setMatchDate(match.getDate().toLocalDate());
                matchTypingRepository.save(typing);
            }
        }
    }

    public void saveAllMatches(List<FootballMatchOutput> matchesFromApi) {
        List<Match> matchEntities = MatchOutputEntityMapper.mapFromOutput(matchesFromApi);
        matchRepository.saveAll(matchEntities);
    }
}
