package com.pszemek.mtjworldcupstandings.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MatchesInputResponse {
    @JsonProperty("data")
    List<FootballMatchInput> matches;
}
