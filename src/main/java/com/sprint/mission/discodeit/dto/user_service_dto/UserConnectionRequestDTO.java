package com.sprint.mission.discodeit.dto.user_service_dto;

import lombok.Getter;

@Getter
public class UserConnectionRequestDTO {
    String userName;

    public UserConnectionRequestDTO(String userName) {
        this.userName = userName;
    }
}
