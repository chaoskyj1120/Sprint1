package com.sprint.mission.discodeit.dto.user_service_dto;

import lombok.Getter;

@Getter
public class UserConnectionRequestDto {
    String userName;

    public UserConnectionRequestDto(String userName) {
        this.userName = userName;
    }
}
