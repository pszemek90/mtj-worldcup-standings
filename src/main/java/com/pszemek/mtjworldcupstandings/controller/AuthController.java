package com.pszemek.mtjworldcupstandings.controller;

import com.pszemek.mtjworldcupstandings.dto.JwtResponse;
import com.pszemek.mtjworldcupstandings.dto.LoginRequest;
import com.pszemek.mtjworldcupstandings.dto.UserDto;
import com.pszemek.mtjworldcupstandings.security.JwtUtils;
import com.pszemek.mtjworldcupstandings.service.CountryAssignmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final CountryAssignmentService countryAssignmentService;

    public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils, CountryAssignmentService countryAssignmentService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.countryAssignmentService = countryAssignmentService;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        UserDto user = (UserDto) authentication.getPrincipal();
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        boolean isFirstLogin = false;

        if(isFirstLogin/*user.getCountry() == null*/){
            isFirstLogin = true;
            countryAssignmentService.assignRandomCountry(user);
        }

        return ResponseEntity.ok(new JwtResponse(jwt, user.getId(), user.getUsername(), user.getEmail(), user.getBalance(), isFirstLogin));
    }
}
