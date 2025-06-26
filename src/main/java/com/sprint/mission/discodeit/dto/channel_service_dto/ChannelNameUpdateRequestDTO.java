package com.sprint.mission.discodeit.dto.channel_service_dto;

import com.sprint.mission.discodeit.dto.user_service_dto.UserDTO;
import lombok.Getter;

@Getter
public class ChannelNameUpdateRequestDTO {
    private final UserDTO userDTO;
    private final ChannelDTO channelDTO;
    private final  String channelNewName;

    public ChannelNameUpdateRequestDTO(UserDTO userDTO, ChannelDTO channelDTO, String channelNewName) {
        this.userDTO = userDTO;
        this.channelDTO = channelDTO;
        this.channelNewName = channelNewName;
    }
}
