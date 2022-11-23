package com.pszemek.mtjworldcupstandings.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MatchTypings {

    private Map<String, List<UserTyping>> matchTypings = new TreeMap<>(Comparator.naturalOrder());

    @JsonAnyGetter
    public Map<String, List<UserTyping>> getMatchTypings() {
        return matchTypings;
    }
}
