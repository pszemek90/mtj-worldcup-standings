package com.pszemek.mtjworldcupstandings.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class TyperScore {
    @JsonProperty("username")
    private String username;
    @JsonProperty("correctTypings")
    private Integer correctTypings;

    public TyperScore setUsername(String username) {
        this.username = username;
        return this;
    }

    public TyperScore setCorrectTypings(Integer correctTypings) {
        this.correctTypings = correctTypings;
        return this;
    }
}
