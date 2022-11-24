package com.pszemek.mtjworldcupstandings.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class UserTyping {
    @JsonProperty("username")
    private String username;
    @JsonProperty("result")
    private String result;
    @JsonProperty("isCorrect")
    private boolean isCorrect;

    public UserTyping setUsername(String username) {
        this.username = username;
        return this;
    }

    public UserTyping setResult(String result) {
        this.result = result;
        return this;
    }

    public UserTyping setCorrect(boolean correct) {
        isCorrect = correct;
        return this;
    }
}
