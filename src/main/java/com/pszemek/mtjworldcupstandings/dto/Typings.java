package com.pszemek.mtjworldcupstandings.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Typings {
    @JsonProperty("matches")
    private List<FootballMatchOutput> matches;
    @JsonProperty("userId")
    private Long userId;
}
