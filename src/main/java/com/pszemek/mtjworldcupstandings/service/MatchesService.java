package com.pszemek.mtjworldcupstandings.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pszemek.mtjworldcupstandings.configuration.CurrentBearerToken;
import com.pszemek.mtjworldcupstandings.dto.*;
import com.pszemek.mtjworldcupstandings.entity.MatchTyping;
import com.pszemek.mtjworldcupstandings.mapper.FootballMatchMapper;
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

    public MatchesService(RestTemplate restTemplate, ApiLoginRequest apiLoginRequest, MatchTypingRepository matchTypingRepository) {
        this.restTemplate = restTemplate;
        this.apiLoginRequest = apiLoginRequest;
        this.matchTypingRepository = matchTypingRepository;
    }

    public List<FootballMatchOutput> getMatchesForToday(String stringDate) {
        logger.info("Getting matches for {}", stringDate);
        LocalDate date = LocalDate.parse(stringDate);
        return getAllMatches().stream()
                .filter(match -> match.getDate().toLocalDate().equals(date))
                .collect(Collectors.toList());
    }

    //todo refactor!
    public List<FootballMatchOutput> getAllMatches() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(CurrentBearerToken.getToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Void> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<MatchesInputResponse> response = null;
        try {
            response = restTemplate.exchange(allMatchesUrl, HttpMethod.GET, httpEntity, MatchesInputResponse.class);
            logger.info("Call to api returned {} code", response.getStatusCode());
            List<FootballMatchInput> matchInputs = response.getBody().getMatches();
            if(matchInputs != null){
                return FootballMatchMapper.mapFromInput(matchInputs);
            }
            return List.of();
        } catch (Exception ex) {
            if(response != null) {
                logger.warn("Call to api returned {} code, getting new Bearer token", response.getStatusCode());
            }
            try {
                getNewBearerToken();
            } catch(HttpClientErrorException ex2) {
                logger.error("API issue, falling back to plain JSON");
                return getMatchesFromPlainJson();
            }
            headers.setBearerAuth(CurrentBearerToken.getToken());
            headers.setContentType(MediaType.APPLICATION_JSON);
            httpEntity = new HttpEntity<>(headers);
            response = restTemplate.exchange(allMatchesUrl, HttpMethod.GET, httpEntity, MatchesInputResponse.class);
            List<FootballMatchInput> matchInputs = response.getBody().getMatches();
            if(matchInputs != null){
                return FootballMatchMapper.mapFromInput(matchInputs);
            }
            return List.of();
        }
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
                return FootballMatchMapper.mapFromInput(matchInputs);
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
                        .setHomeScore(match.getHomeScore())
                        .setMatchId(match.getId());
                matchTypingRepository.save(typing);
            }
        }
    }
}
