package com.pszemek.mtjworldcupstandings.service;

import com.pszemek.mtjworldcupstandings.entity.User;
import com.pszemek.mtjworldcupstandings.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
}
