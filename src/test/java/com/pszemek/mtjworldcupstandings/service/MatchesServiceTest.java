package com.pszemek.mtjworldcupstandings.service;

import com.pszemek.mtjworldcupstandings.dto.BearerTokenDto;
import com.pszemek.mtjworldcupstandings.dto.LoginRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MatchesServiceTest {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private LoginRequest loginRequest;

    @Test
    void getBearerTokenTest() {
        HttpEntity<LoginRequest> request = new HttpEntity<>(loginRequest);

        ResponseEntity<BearerTokenDto> bearerTokenResponseEntity = restTemplate.postForEntity("http://api.cup2022.ir/api/v1/user/login", request, BearerTokenDto.class);
        HttpStatus statusCode = bearerTokenResponseEntity.getStatusCode();
        assertEquals(HttpStatus.OK, statusCode);
    }

}
