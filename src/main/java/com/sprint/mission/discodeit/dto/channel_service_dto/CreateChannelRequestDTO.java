package com.sprint.mission.discodeit.dto.channel_service_dto;

import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateChannelRequestDTO {

    UUID hostUserId;
    String channelName;
    String description;

    public CreateChannelRequestDTO(UUID hostUserId, String channelName, String description) {
        this.hostUserId = hostUserId;
        this.channelName = channelName;
        this.description = description;
    }
}
