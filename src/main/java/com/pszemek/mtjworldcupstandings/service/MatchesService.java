package com.pszemek.mtjworldcupstandings.service;

import com.pszemek.mtjworldcupstandings.configuration.CurrentBearerToken;
import com.pszemek.mtjworldcupstandings.dto.*;
import com.pszemek.mtjworldcupstandings.entity.MatchTyping;
import com.pszemek.mtjworldcupstandings.mapper.FootballMatchMapper;
import com.pszemek.mtjworldcupstandings.repository.MatchTypingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MatchesService {

    private final Logger logger = LoggerFactory.getLogger(MatchesService.class);

    private final RestTemplate restTemplate;
    private final LoginRequest loginRequest;

    private final MatchTypingRepository matchTypingRepository;

    public MatchesService(RestTemplate restTemplate, LoginRequest loginRequest, MatchTypingRepository matchTypingRepository) {
        this.restTemplate = restTemplate;
        this.loginRequest = loginRequest;
        this.matchTypingRepository = matchTypingRepository;
    }

    //todo refactor!
    public List<FootballMatchOutput> getAllMatches() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(CurrentBearerToken.getToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Void> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<MatchesInputResponse> response = null;
        try {
            response = restTemplate.exchange("http://api.cup2022.ir/api/v1/match", HttpMethod.GET, httpEntity, MatchesInputResponse.class);
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
            getNewBearerToken();
            headers.setBearerAuth(CurrentBearerToken.getToken());
            headers.setContentType(MediaType.APPLICATION_JSON);
            httpEntity = new HttpEntity<>(headers);
            response = restTemplate.exchange("http://api.cup2022.ir/api/v1/match", HttpMethod.GET, httpEntity, MatchesInputResponse.class);
            List<FootballMatchInput> matchInputs = response.getBody().getMatches();
            if(matchInputs != null){
                return FootballMatchMapper.mapFromInput(matchInputs);
            }
            return List.of();
        }
    }

    public List<FootballMatchOutput> getMatchesForToday(String stringDate) {
        logger.info("Getting matches for {}", stringDate);
        LocalDate date = LocalDate.parse(stringDate);
        return getAllMatches().stream()
                .filter(match -> match.getDate().toLocalDate().equals(date))
                .collect(Collectors.toList());
    }

    private void getNewBearerToken() {
        HttpEntity<LoginRequest> request = new HttpEntity<>(loginRequest);
        ResponseEntity<BearerTokenDto> bearerTokenResponseEntity = restTemplate.postForEntity("http://api.cup2022.ir/api/v1/user/login", request, BearerTokenDto.class);
        BearerTokenDto responseBody = bearerTokenResponseEntity.getBody();
        if (responseBody != null && responseBody.getToken() != null) {
            logger.info("Setting new Bearer token");
            CurrentBearerToken.setToken(responseBody.getToken());
        } else {
            logger.error("Something went wrong with login response. Returned body: {} ", responseBody);
        }
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
