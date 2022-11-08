package com.pszemek.mtjworldcupstandings.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class InputTypings {
    @JsonProperty("matches")
    private List<FootballMatchOutput> matches;
    @JsonProperty("userId")
    private Long userId;

    @JsonIgnore
    @Override
    public String toString() {
        return "user Id: " + userId + " matches: " + matches.toString();
    }
}
