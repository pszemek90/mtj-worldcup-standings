package com.pszemek.mtjworldcupstandings.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApiLoginRequest {

    @Value("${login.email}")
    @JsonProperty("email")
    private String email;

    @Value("${login.password}")
    @JsonProperty("password")
    private String password;

}
