package com.pszemek.mtjworldcupstandings.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class ChangePasswordRequest {
    @JsonProperty("userId")
    private Long userId;
    @JsonProperty("newPassword")
    private String newPassword;
    @JsonProperty("oldPassword")
    private String oldPassword;
}
