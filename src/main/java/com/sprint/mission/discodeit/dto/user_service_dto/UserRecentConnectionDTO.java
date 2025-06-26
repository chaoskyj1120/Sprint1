package com.sprint.mission.discodeit.dto.user_service_dto;

import lombok.Getter;

import java.util.UUID;

@Getter
public class UserRecentConnectionDTO {
    private final UUID id;
    private final String userName;
    private final String isLoggedIn;

    public UserRecentConnectionDTO(UUID id, String userName, String loggedIn) {
        this.id = id;
        this.userName = userName;
        this.isLoggedIn = loggedIn;
    }
}
