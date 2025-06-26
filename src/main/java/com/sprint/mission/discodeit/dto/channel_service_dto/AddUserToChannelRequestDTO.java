package com.sprint.mission.discodeit.dto.channel_service_dto;

import com.sprint.mission.discodeit.dto.user_service_dto.UserDTO;
import lombok.Getter;

@Getter
public class AddUserToChannelRequestDTO {
    UserDTO userDTO;
    ChannelDTO channelDTO;

    public AddUserToChannelRequestDTO(UserDTO userDTO, ChannelDTO channelDTO) {
        this.userDTO = userDTO;
        this.channelDTO = channelDTO;
    }
}
