package com.pszemek.mtjworldcupstandings.configuration;

public class CurrentBearerToken {

    private static String token = "";

    public static String getToken() {
        return token;
    }

    public static void setToken(String newToken) {
        token = newToken;
    }
}
