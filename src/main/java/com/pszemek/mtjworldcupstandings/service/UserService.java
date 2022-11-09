package com.pszemek.mtjworldcupstandings.service;

import com.pszemek.mtjworldcupstandings.dto.ChangePasswordRequest;
import com.pszemek.mtjworldcupstandings.dto.UserDto;
import com.pszemek.mtjworldcupstandings.entity.User;
import com.pszemek.mtjworldcupstandings.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.math.BigDecimal;
import java.util.Optional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public UserService(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    public void addWinningAmount(Long userId, BigDecimal amount) {
        logger.info("Adding balance of: {} for user: {}", amount, userId);
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isPresent()) {
            User user = userOptional.get();
            user.setBalance(user.getBalance().add(amount));
            userRepository.save(user);
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
}
