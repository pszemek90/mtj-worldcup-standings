package com.pszemek.mtjworldcupstandings.service;

import com.pszemek.mtjworldcupstandings.dto.FootballMatchOutput;
import com.pszemek.mtjworldcupstandings.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest
class UpdateResultsTest {

    @Autowired
    private UpdateResults updateResultsService;
    @MockBean
    private UserService userService;
    @MockBean
    private MatchesService matchesService;
    @MockBean
    private OverallPoolService overallPoolService;

    @Test
    public void threeUsersNoFinishedMatchesNoPool() {
        List<User> users = List.of(new User().setBalance(BigDecimal.valueOf(65L)),
                new User().setBalance(BigDecimal.valueOf(65L)),
                new User().setBalance(BigDecimal.valueOf(65L)));
        when(userService.getAllUsers()).thenReturn(users);
        updateResultsService.validateCashPool(List.of(), BigDecimal.ZERO);
    }

    @Test
    public void threeUsersThreeFinishedMatchesNoPool() {
        List<User> users = List.of(new User().setBalance(BigDecimal.valueOf(62L)),
                new User().setBalance(BigDecimal.valueOf(62L)),
                new User().setBalance(BigDecimal.valueOf(63L)));
        when(userService.getAllUsers()).thenReturn(users);
        List<FootballMatchOutput> finishedMatches = List.of(
                new FootballMatchOutput().setPool(BigDecimal.valueOf(3L)),
                new FootballMatchOutput().setPool(BigDecimal.valueOf(3L)),
                new FootballMatchOutput().setPool(BigDecimal.valueOf(2L))
        );
        updateResultsService.validateCashPool(finishedMatches, BigDecimal.ZERO);
    }

    @Test
    public void threeUsersThreeFinishedMatchesWithPool() {
        List<User> users = List.of(new User().setBalance(BigDecimal.valueOf(57L)),
                new User().setBalance(BigDecimal.valueOf(57L)),
                new User().setBalance(BigDecimal.valueOf(58L)));
        when(userService.getAllUsers()).thenReturn(users);
        List<FootballMatchOutput> finishedMatches = List.of(
                new FootballMatchOutput().setPool(BigDecimal.valueOf(3L)),
                new FootballMatchOutput().setPool(BigDecimal.valueOf(3L)),
                new FootballMatchOutput().setPool(BigDecimal.valueOf(2L))
        );
        updateResultsService.validateCashPool(finishedMatches, BigDecimal.valueOf(15L));
    }

    @Test
    void checkPoolSplitForNoWinners() {
        //given
        List<Long> winners = List.of();
        FootballMatchOutput finishedMatch = new FootballMatchOutput().setHomeTeam("test1").setAwayTeam("test2").setPool(BigDecimal.valueOf(10L));
        BigDecimal poolShare = BigDecimal.ZERO;
        //when, then
        updateResultsService.splitPool(winners, finishedMatch, poolShare);
    }

    @Test
    void checkPoolForOneWinner() {
        //given
        List<Long> winners = List.of(1L);
        FootballMatchOutput finishedMatch = new FootballMatchOutput().setHomeTeam("test1").setAwayTeam("test2").setPool(BigDecimal.valueOf(10L));
        BigDecimal poolShare = BigDecimal.ZERO;
        //when, then
        updateResultsService.splitPool(winners, finishedMatch, poolShare);
    }
}
