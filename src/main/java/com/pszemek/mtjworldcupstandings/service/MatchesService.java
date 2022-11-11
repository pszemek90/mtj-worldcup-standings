package com.pszemek.mtjworldcupstandings.service;

import com.pszemek.mtjworldcupstandings.dto.FootballMatchOutput;
import com.pszemek.mtjworldcupstandings.dto.InputTypings;
import com.pszemek.mtjworldcupstandings.entity.Match;
import com.pszemek.mtjworldcupstandings.entity.MatchTyping;
import com.pszemek.mtjworldcupstandings.enums.TypingResultEnum;
import com.pszemek.mtjworldcupstandings.mapper.MatchOutputEntityMapper;
import com.pszemek.mtjworldcupstandings.mapper.MatchTypingFootballMatchOutputMapper;
import com.pszemek.mtjworldcupstandings.repository.MatchRepository;
import com.pszemek.mtjworldcupstandings.repository.MatchTypingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.persistence.PersistenceException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MatchesService {

    private final Logger logger = LoggerFactory.getLogger(MatchesService.class);

    private final MatchTypingRepository matchTypingRepository;
    private final MatchRepository matchRepository;
    private final UserService userService;

    public MatchesService(MatchTypingRepository matchTypingRepository, MatchRepository matchRepository, UserService userService) {
        this.matchTypingRepository = matchTypingRepository;
        this.matchRepository = matchRepository;
        this.userService = userService;
    }

    public List<FootballMatchOutput> getMatchesForToday(String stringDate) {
        logger.info("Getting matches for {}", stringDate);
        LocalDate date = LocalDate.parse(stringDate);
        return getAllMatches().stream()
                .filter(match -> match.getDate().toLocalDate().equals(date))
                .collect(Collectors.toList());
    }

    public List<FootballMatchOutput> getAllMatches() {
        List<Match> matchesFromDb = matchRepository.findAll();
        return MatchOutputEntityMapper.mapFromEntity(matchesFromDb);
    }

    public void saveTypings(InputTypings inputTypings) {
        List<FootballMatchOutput> matches = inputTypings.getMatches();
        logger.info("Typed matches amount: {}", matches.size());
        Long userId = inputTypings.getUserId();
        for(FootballMatchOutput match : matches) {
            Optional<MatchTyping> matchAlreadyTyped = matchTypingRepository.findByUserIdAndMatchId(userId, match.getId());
            if(matchAlreadyTyped.isPresent()){
                logger.info("Match {} - {} was already typed", match.getHomeTeam(), match.getAwayTeam());
                MatchTyping matchToUpdate = matchAlreadyTyped.get()
                        .setAwayScore(match.getAwayScore())
                        .setHomeScore(match.getHomeScore());
                matchTypingRepository.save(matchToUpdate);
            } else {
                logger.info("Match {} - {} is typed for the first time", match.getHomeTeam(), match.getAwayTeam());
                MatchTyping typing = MatchTypingFootballMatchOutputMapper.mapToEntity(match);
                typing.setUserId(userId);
                typing.setStatus(TypingResultEnum.UNKNOWN);
                matchTypingRepository.save(typing);
                lowerBalanceByOne(userId, typing);
                raisePoolByOne(match);
            }
        }
    }

    private void raisePoolByOne(FootballMatchOutput match) {
        Optional<Match> matchEntityOptional = matchRepository.findByMatchId(match.getId());
        if(matchEntityOptional.isPresent()) {
            Match matchEntity = matchEntityOptional.get();
            matchEntity.setPool(matchEntity.getPool().add(BigDecimal.ONE));
            matchRepository.save(matchEntity);
        } else {
            logger.error("Couldn't found a match with id: {}", match.getId());
            throw new PersistenceException("Couldn't found a match with id: " + match.getId());
        }
    }

    private void lowerBalanceByOne(Long userId, MatchTyping typing) {
        userService.lowerBalanceByOne(userId, typing);
    }

    public void saveAllMatches(List<FootballMatchOutput> matchesToAddOrUpdate) {
        for(FootballMatchOutput match : matchesToAddOrUpdate) {
            Optional<Match> matchToUpdateOptional = matchRepository.findByMatchId(match.getId());
            Match newMatch = MatchOutputEntityMapper.mapFromOutput(match);
            if(matchToUpdateOptional.isPresent()) {
                Match oldMatch = matchToUpdateOptional.get();
                updateOldMatch(oldMatch, newMatch);
                matchRepository.save(oldMatch);
            } else {
                matchRepository.save(newMatch);
            }
        }
    }

    private void updateOldMatch(Match oldMatch, Match newMatch) {
        oldMatch.setHomeScore(newMatch.getHomeScore())
                .setAwayScore(newMatch.getAwayScore())
                .setFinished(newMatch.getFinished());
    }

    public void clearMatchPool(FootballMatchOutput finishedMatch) {
        Optional<Match> matchEntityOptional = matchRepository.findByMatchId(finishedMatch.getId());
        if(matchEntityOptional.isPresent()) {
            logger.info("Clearing pool for match: {} - {}", finishedMatch.getHomeTeam(), finishedMatch.getAwayTeam());
            Match match = matchEntityOptional.get();
            match.setPool(BigDecimal.ZERO);
            matchRepository.save(match);
        } else {
            logger.error("Couldn't find match with id: {} in database", finishedMatch.getId());
            throw new IllegalStateException("Couldn't find match in database!");
        }
    }
}
