package com.pszemek.mtjworldcupstandings.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pszemek.mtjworldcupstandings.configuration.CurrentBearerToken;
import com.pszemek.mtjworldcupstandings.dto.*;
import com.pszemek.mtjworldcupstandings.entity.MatchTyping;
import com.pszemek.mtjworldcupstandings.entity.User;
import com.pszemek.mtjworldcupstandings.enums.TypingResultEnum;
import com.pszemek.mtjworldcupstandings.mapper.MatchInputOutputMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service with scheduled method to update matches state basing on api, split pool for winner and store cash for matches
 * with no correct typings. Methods with unit tests are with package private scope.
 */
@Component
public class UpdateResults {
    private static final Logger logger = LoggerFactory.getLogger(UpdateResults.class);

    @Value("${api.all.matches.url}")
    private String allMatchesUrl;

    @Value("${api.login.url}")
    private String loginUrl;

    @Value("${fallback.json.path}")
    private String fallbackJsonPath;

    private final RestTemplate restTemplate;
    private final ApiLoginRequest apiLoginRequest;
    private final MatchesService matchesService;
    private final TypingsService typingsService;
    private final UserService userService;
    private final OverallPoolService overallPoolService;

    public UpdateResults(RestTemplate restTemplate, ApiLoginRequest apiLoginRequest, MatchesService matchesService, TypingsService typingsService, UserService userService, OverallPoolService overallPoolService) {
        this.restTemplate = restTemplate;
        this.apiLoginRequest = apiLoginRequest;
        this.matchesService = matchesService;
        this.typingsService = typingsService;
        this.userService = userService;
        this.overallPoolService = overallPoolService;
    }

    @Scheduled(cron = "0 15 0 * * *")
    @Retryable(value = {HttpServerErrorException.class}, maxAttempts = 10, backoff = @Backoff(delay = 300000))
    public void getCurrentMatches() {
        logger.info("Getting current matches triggered");
        getBearerToken();
        List<FootballMatchOutput> matchesFromApi = getMatches();
        //todo for testing purposes
//        List<FootballMatchOutput> matchesFromApi = getMatchesFromPlainJson();
        List<FootballMatchOutput> matchesFromDb = matchesService.getAllMatches();
        List<FootballMatchOutput> matchesToUpdate = compareAndUpdateMatches(matchesFromApi, matchesFromDb);
        if (!matchesToUpdate.isEmpty()) {
            updateTypings();
        } else {
            logger.info("No typings update today");
        }
    }

    private List<FootballMatchOutput> compareAndUpdateMatches(List<FootballMatchOutput> matchesFromApi, List<FootballMatchOutput> matchesFromDb) {
        logger.info("Comparing and updating matches");
        List<FootballMatchOutput> matchesToAddOrUpdate =
                matchesFromApi.stream()
                        .filter(match -> !matchesFromDb.contains(match))
                        .collect(Collectors.toList());

        if(!matchesToAddOrUpdate.isEmpty()) {
            logger.info("Adding or updating matches:");
            matchesToAddOrUpdate.forEach(match -> logger.info(
                    "{} {} - {} {}, finished: {}",
                    match.getHomeTeam(), match.getHomeScore(), match.getAwayScore(), match.getAwayTeam(),
                    match.isFinished()));
            matchesService.saveAllMatches(matchesToAddOrUpdate);
        } else {
            logger.info("No matches to update or add");
        }
        return matchesToAddOrUpdate;
    }

    private void updateTypings() {
        logger.info("Updating typings");
        List<FootballMatchOutput> matchesFromDb = matchesService.getAllMatches();
        List<FootballMatchOutput> recentlyFinishedMatches = matchesFromDb.stream()
                .filter(FootballMatchOutput::getFinished)
                .filter(match -> match.getPool().compareTo(BigDecimal.ZERO) > 0)
                .collect(Collectors.toList());
        logger.info("Recently finished matches: {}", recentlyFinishedMatches);
        List<MatchTyping> allTypings = typingsService.getAllTypingEntities()
                .stream().filter(typing -> typing.getStatus() == TypingResultEnum.UNKNOWN)
                .collect(Collectors.toList());
        BigDecimal todaysPool = overallPoolService.getOverallPool().getAmount();
        logger.info("Todays overall pool: {}", todaysPool);
        logger.info("Validating overall cash amount before split");
        try {
            // not so critical to stop updating typings
            validateCashPool(recentlyFinishedMatches, todaysPool);
        } catch (Exception e) {
            logger.error("Cash pool validation failed. Cause: {}", e.getMessage());
        }
        if(!recentlyFinishedMatches.isEmpty()) {
            BigDecimal poolShareForMatch = todaysPool.divide(BigDecimal.valueOf(recentlyFinishedMatches.size()), 2, RoundingMode.HALF_UP);
            logger.info("Todays pool share for one match: {}. Number of matches: {}", poolShareForMatch, recentlyFinishedMatches.size());
            logger.info("Subtracting {} from overall pool", todaysPool);
            overallPoolService.clearOverallPool();
            for(FootballMatchOutput finishedMatch : recentlyFinishedMatches) {
                List<MatchTyping> typingsForCheck = allTypings.stream()
                        .filter(typing -> typing.getMatchId().equals(finishedMatch.getId()))
                        .collect(Collectors.toList());
                List<Long> winners = checkWinners(typingsForCheck, finishedMatch);
                splitPool(winners, finishedMatch, poolShareForMatch);
            }
            typingsService.saveAll(allTypings);
        }
        BigDecimal nextDayPool = overallPoolService.getOverallPool().getAmount();
        logger.info("Overall pool for next day: {}", nextDayPool);
        logger.info("Validating overall cash amount after split");
        try {
            validateCashPool(List.of(), nextDayPool);
        } catch (Exception e) {
            logger.error("Cash pool validation failed. Cause: {}", e.getMessage());
        }
    }

    //package private scope for testing
    void validateCashPool(List<FootballMatchOutput> recentlyFinishedMatches, BigDecimal todaysPool) {
        BigDecimal matchesPool = BigDecimal.ZERO;
        if(!recentlyFinishedMatches.isEmpty()){
            matchesPool = recentlyFinishedMatches.stream()
                    .map(FootballMatchOutput::getPool)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            logger.info("Today's pool from finished matches: {}", matchesPool);
        }
        List<User> allUsers = userService.getAllUsers();
        BigDecimal usersBalance = allUsers.stream().map(User::getBalance).reduce(BigDecimal.ZERO, BigDecimal::add);
        logger.info("Today's users balance: {}", usersBalance);
        logger.info("Today's pool: {}", todaysPool);
        BigDecimal todaysCash = matchesPool.add(usersBalance).add(todaysPool);
        logger.info("Today's cash: {}", todaysCash);
        BigDecimal startingBalance = BigDecimal.valueOf(65L);
        BigDecimal userCount = BigDecimal.valueOf(allUsers.size());
        BigDecimal startingCash = startingBalance.multiply(userCount);
        logger.info("Starting cash: {}", startingCash);
        // if difference between today's cash and starting cash is greater than 1 then warn
        if(todaysCash.subtract(startingCash).abs().compareTo(BigDecimal.ONE) > 0){
            logger.warn("Starting cash and today's cash doesn't match");
        }
    }

    void splitPool(List<Long> winners, FootballMatchOutput finishedMatch, BigDecimal poolShare) {
        logger.info("Splitting pool");
        BigDecimal poolFromMatch = finishedMatch.getPool();
        if(!winners.isEmpty()) {
            logger.info("Splitting pool for match: {} - {}", finishedMatch.getHomeTeam(), finishedMatch.getAwayTeam());
            BigDecimal splitFromMatch = poolFromMatch.divide(BigDecimal.valueOf(winners.size()), 2, RoundingMode.HALF_UP);
            BigDecimal splitFromPoolShare = poolShare.divide(BigDecimal.valueOf(winners.size()), 2, RoundingMode.HALF_UP);
            BigDecimal split = splitFromMatch.add(splitFromPoolShare);
            for(Long userId : winners) {
                userService.addWinningAmount(userId, split, finishedMatch);
            }
        } else {
            logger.info("No winners for match: {} - {}", finishedMatch.getHomeTeam(), finishedMatch.getAwayTeam());
            BigDecimal pool = poolFromMatch.add(poolShare);
            overallPoolService.riseOverallPool(pool);
        }
        matchesService.clearMatchPool(finishedMatch);
    }

    private List<Long> checkWinners(List<MatchTyping> typingsForCheck, FootballMatchOutput finishedMatch) {
        logger.info("Checking winners for today");
        Integer matchHomeScore = finishedMatch.getHomeScore();
        Integer matchAwayScore = finishedMatch.getAwayScore();
        List<Long> winners = new ArrayList<>();
        for(MatchTyping typing : typingsForCheck) {
            Integer typingHomeScore = typing.getHomeScore();
            Integer typingAwayScore = typing.getAwayScore();
            if(typingHomeScore.equals(matchHomeScore) && typingAwayScore.equals(matchAwayScore)) {
                logger.info("Winner typing for user: {}, for match: {} - {}",
                        typing.getUserId(), finishedMatch.getHomeTeam(), finishedMatch.getAwayTeam());
                typing.setStatus(TypingResultEnum.CORRECT);
                winners.add(typing.getUserId());
            } else {
                typing.setStatus(TypingResultEnum.INCORRECT);
            }
        }
        logger.info("Overall winners for match: {} - {}: {}", finishedMatch.getHomeTeam(), finishedMatch.getAwayTeam(),
                winners.size());
        return winners;
    }

    private List<FootballMatchOutput> getMatches() {
        logger.info("Getting matches from API");
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(CurrentBearerToken.getToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Void> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<MatchesInputResponse> response;
        try {
            logger.info("Calling matches API");
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
            logger.info("Calling login API");
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
}
