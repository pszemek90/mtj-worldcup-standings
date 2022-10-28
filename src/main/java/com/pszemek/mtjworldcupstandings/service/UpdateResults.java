package com.pszemek.mtjworldcupstandings.service;

import com.pszemek.mtjworldcupstandings.configuration.CurrentBearerToken;
import com.pszemek.mtjworldcupstandings.dto.*;
import com.pszemek.mtjworldcupstandings.mapper.MatchInputOutputMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UpdateResults {
    private static final Logger logger = LoggerFactory.getLogger(UpdateResults.class);

    @Value("${api.all.matches.url}")
    private String allMatchesUrl;

    @Value("${api.login.url}")
    private String loginUrl;

    private final RestTemplate restTemplate;
    private final ApiLoginRequest apiLoginRequest;
    private final MatchesService matchesService;

    public UpdateResults(RestTemplate restTemplate, ApiLoginRequest apiLoginRequest, MatchesService matchesService) {
        this.restTemplate = restTemplate;
        this.apiLoginRequest = apiLoginRequest;
        this.matchesService = matchesService;
    }

    @Scheduled(cron = "0 57 22 * * *")
    private void getCurrentMatches() {
        getBearerToken();
        List<FootballMatchOutput> matchesFromApi = getMatches();
        List<FootballMatchOutput> matchesFromDb = matchesService.getAllMatches();
        if(matchesFromDb.isEmpty())
            matchesService.saveAllMatches(matchesFromApi);
        else
            compareAndUpdateMatches(matchesFromApi, matchesFromDb);
    }

    private void compareAndUpdateMatches(List<FootballMatchOutput> matchesFromApi, List<FootballMatchOutput> matchesFromDb) {
        List<FootballMatchOutput> matchesToAddOrUpdate =
                matchesFromApi.stream()
                        .filter(match -> !matchesFromDb.contains(match))
                        .collect(Collectors.toList());

        if(!matchesToAddOrUpdate.isEmpty()) {
            logger.info("Adding or updating matches:");
            matchesToAddOrUpdate.forEach(match -> logger.info(
                    "{} {} - {} {}, state: {}",
                    match.getHomeTeam(), match.getHomeScore(), match.getAwayScore(), match.getAwayTeam(),
                    match.isFinished()));
            matchesService.saveAllMatches(matchesToAddOrUpdate);
        } else
            logger.info("No matches to update or add");
    }

    private List<FootballMatchOutput> getMatches() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(CurrentBearerToken.getToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Void> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<MatchesInputResponse> response;
        try {
            response = restTemplate.exchange(allMatchesUrl, HttpMethod.GET, httpEntity, MatchesInputResponse.class);
            logger.info("Successful call to all matches api");
        } catch (Exception ex) {
            logger.error("Exception while fetching all matches from api: {}", ex.getMessage());
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while fetching all matches from api");
        }
        List<FootballMatchInput> matchInputs = response.getBody().getMatches();
        if(matchInputs != null){
            return MatchInputOutputMapper.mapFromInput(matchInputs);
        }
        logger.warn("Fetched matches was null, returning empty list.");
        return List.of();
    }

    private void getBearerToken() {
        HttpEntity<ApiLoginRequest> request = new HttpEntity<>(apiLoginRequest);
        ResponseEntity<BearerTokenDto> bearerTokenResponseEntity;
        try {
            bearerTokenResponseEntity = restTemplate.postForEntity(loginUrl, request, BearerTokenDto.class);
        } catch (Exception ex) {
            logger.error("Exception while getting new Bearer token from api: {}", ex.getMessage());
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while fetching bearer token from api");
        }
        BearerTokenDto responseBody = bearerTokenResponseEntity.getBody();
        if (responseBody != null && responseBody.getToken() != null) {
            logger.info("Setting new Bearer token");
            CurrentBearerToken.setToken(responseBody.getToken());
        } else {
            logger.error("Something went wrong with login response. Returned body: {} ", responseBody);
        }
    }

    private void markFinishedMatches() {

    }
}
