package com.pszemek.mtjworldcupstandings.service;

import com.pszemek.mtjworldcupstandings.dto.AccountHistoryPageRequest;
import com.pszemek.mtjworldcupstandings.dto.ChangePasswordRequest;
import com.pszemek.mtjworldcupstandings.dto.FootballMatchOutput;
import com.pszemek.mtjworldcupstandings.dto.UserDto;
import com.pszemek.mtjworldcupstandings.entity.AccountHistory;
import com.pszemek.mtjworldcupstandings.entity.MatchTyping;
import com.pszemek.mtjworldcupstandings.entity.User;
import com.pszemek.mtjworldcupstandings.mapper.AccountHistoryMapper;
import com.pszemek.mtjworldcupstandings.repository.AccountHistoryRepository;
import com.pszemek.mtjworldcupstandings.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final AccountHistoryRepository accountHistoryRepository;
    private final PasswordEncoder encoder;

    public UserService(UserRepository userRepository, AccountHistoryRepository accountHistoryRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.accountHistoryRepository = accountHistoryRepository;
        this.encoder = encoder;
    }

    public void addWinningAmount(Long userId, BigDecimal amount, FootballMatchOutput match) {
        logger.info("Adding balance of: {} for user: {}", amount, userId);
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isPresent()) {
            User user = userOptional.get();
            user.setBalance(user.getBalance().add(amount));
            userRepository.save(user);
            String message = "Wygrana z meczu: " + match.getHomeTeam() + " - " + match.getAwayTeam();
            logEvent(userId, message, amount);
        } else {
            logger.error("User with id: {} not found", userId);
            throw new UsernameNotFoundException("Couldn't found user with Id: " + userId);
        }
    }

    public User getByUserId(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isPresent()) {
            return userOptional.get();
        } else {
            logger.error("Couldn't find user with id: {} in database", userId);
            throw new UsernameNotFoundException("Couldn't find user in database");
        }
    }

    public BigDecimal getUserBalanceById(Long userId) {
        User user = getByUserId(userId);
        return user.getBalance();
    }

    public void setUsersCountry(UserDto userDto) {
        logger.info("Setting country for user with id: {}", userDto.getId());
        User user = getByUserId(userDto.getId());
        user.setCountry(userDto.getCountry());
        userRepository.save(user);
    }

    public User saveUser(User user) {
        logger.info("Saving or updating user with id: {}", user.getId());
        return userRepository.save(user);
    }

    public void lowerBalanceByOne(Long userId, MatchTyping typing) {
        logger.info("Lowering balance for userId: {}", userId);
        User user = getByUserId(userId);
        user.setBalance(user.getBalance().subtract(BigDecimal.ONE));
        saveUser(user);
        String message = "Obastwiony mecz: " + typing.getHomeTeam() + " - " + typing.getAwayTeam();
        logEvent(userId, message, BigDecimal.valueOf(-1L));
    }

    public User changePassword(ChangePasswordRequest request) throws AuthenticationException {
        logger.info("Change password requested by user id: {}", request.getUserId());
        User user = getByUserId(request.getUserId());
        UserDto principal = (UserDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(!encoder.matches(request.getOldPassword(), principal.getPassword())) {
            throw new AuthenticationException();
        }
        user.setPassword(encoder.encode(request.getNewPassword()));
        return saveUser(user);
    }

    public void logEvent(Long userId, String message, BigDecimal difference) {
        AccountHistory event = new AccountHistory(message, difference, userId);
        accountHistoryRepository.save(event);
    }

    public AccountHistoryPageRequest getAccountHistoryForUser(Long userId, int pageNumber) {
        Sort sortByDate = Sort.by("timestamp").descending();
        Pageable page = PageRequest.of(pageNumber - 1, 10, sortByDate);
        Page<AccountHistory> historyPage = accountHistoryRepository.findByUserId(page, userId);
        return new AccountHistoryPageRequest()
                .setTotalAmount(historyPage.getTotalElements())
                .setHistory(AccountHistoryMapper.mapFromEntity(historyPage.get().collect(Collectors.toList())));
    }
}
