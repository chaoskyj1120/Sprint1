package com.sprint.mission.discodeit.dto;

import lombok.Getter;

import java.util.UUID;

@Getter
public class UserLoggedDataDTO {
    private UUID id;
    private String userId;
    private String userName;
    private String isLoggedIn;

    public UserLoggedDataDTO(UUID id, String idForLogin, String userName, String loggedIn) {
        this.id = id;
        this.userId = idForLogin;
        this.userName = userName;
        this.isLoggedIn = loggedIn;
    }
}
