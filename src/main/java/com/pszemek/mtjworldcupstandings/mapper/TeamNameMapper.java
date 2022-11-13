package com.pszemek.mtjworldcupstandings.mapper;

import java.util.Map;

public class TeamNameMapper {
    public static String mapTeam(String enTeam) {
        return countryMap.getOrDefault(enTeam, enTeam);
    }

    private static final Map<String, String> countryMap =
            Map.ofEntries(Map.entry("England", "Anglia"),
                    Map.entry("Qatar", "Katar"),
                    Map.entry("United States", "Stany Zjednoczone"),
                    Map.entry("Argentina", "Argentyna"),
                    Map.entry("Denmark", "Dania"),
                    Map.entry("Mexico", "Meksyk"),
                    Map.entry("France", "Francja"),
                    Map.entry("Morocco", "Maroko"),
                    Map.entry("Germany", "Niemcy"),
                    Map.entry("Spain", "Hiszpania"),
                    Map.entry("Belgium", "Belgia"),
                    Map.entry("Brazil", "Brazylia"),
                    Map.entry("Portugal", "Portugalia"),
                    Map.entry("Uruguay", "Urugwaj"),
                    Map.entry("Switzerland", "Szwajcaria"),
                    Map.entry("Nederlands", "Holandia"),
                    Map.entry("Tunisia", "Tunezja"),
                    Map.entry("Poland", "Polska"),
                    Map.entry("Japan", "Japonia"),
                    Map.entry("Croatia", "Chorwacja"),
                    Map.entry("South Korea", "Korea Południowa"),
                    Map.entry("Ecuador", "Ekwador"),
                    Map.entry("Saudi Arabia", "Arabia Saudyjska"),
                    Map.entry("Costa Rica", "Kostaryka"),
                    Map.entry("Cameroon", "Kamerun"),
                    Map.entry("Wales", "Walia"),
                    Map.entry("Canada", "Kanada"));

}
