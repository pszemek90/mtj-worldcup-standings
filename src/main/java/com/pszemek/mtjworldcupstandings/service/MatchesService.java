package com.pszemek.mtjworldcupstandings.service;

import com.pszemek.mtjworldcupstandings.dto.FootballMatchOutput;
import com.pszemek.mtjworldcupstandings.dto.Typings;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MatchesService {

    private final Logger logger = LoggerFactory.getLogger(MatchesService.class);

    private final MatchTypingRepository matchTypingRepository;
    private final MatchRepository matchRepository;

    public MatchesService(MatchTypingRepository matchTypingRepository, MatchRepository matchRepository) {
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
                MatchTyping typing = MatchTypingFootballMatchOutputMapper.mapToEntity(match);
                typing.setUserId(userId);
                typing.setStatus(TypingResultEnum.UNKNOWN);
                matchTypingRepository.save(typing);
            }
        }
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
}
