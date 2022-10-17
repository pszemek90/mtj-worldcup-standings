package com.pszemek.mtjworldcupstandings.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Map;

@Getter
public class BearerTokenDto {

    private String token;

    @JsonProperty("data")
    private void unpackToken(Map<String, String> data) {
        this.token = data.get("token");
    }
}
