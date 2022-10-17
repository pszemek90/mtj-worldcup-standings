package com.pszemek.mtjworldcupstandings.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class MatchesInputResponse {
    @JsonProperty("data")
    List<FootballMatchInput> matches;
}
