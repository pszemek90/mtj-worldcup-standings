package com.pszemek.mtjworldcupstandings.controller;

import com.pszemek.mtjworldcupstandings.dto.AccountHistoryDto;
import com.pszemek.mtjworldcupstandings.dto.ChangePasswordRequest;
import com.pszemek.mtjworldcupstandings.entity.User;
import com.pszemek.mtjworldcupstandings.mapper.AccountHistoryMapper;
import com.pszemek.mtjworldcupstandings.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import java.math.BigDecimal;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/balance")
    public BigDecimal getUserBalance(Long userId) {
        return userService.getUserBalanceById(userId);
    }

    @PostMapping("/changePassword")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        try {
            User user = userService.changePassword(request);
            if (user != null) {
                logger.info("User id: {}. Password changed successfully", request.getUserId());
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.internalServerError().build();
            }
        } catch (AuthenticationException e) {
            logger.info("Old password doesn't match users password. Returning unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/history")
    public List<AccountHistoryDto> getUsersHistory(Long userId) {
        logger.info("Getting account history for user: {}", userId);
        return AccountHistoryMapper.mapFromEntity(userService.getAccountHistoryForUser(userId));
    }
}
