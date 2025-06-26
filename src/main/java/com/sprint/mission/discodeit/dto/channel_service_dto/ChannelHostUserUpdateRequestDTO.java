package com.sprint.mission.discodeit.dto.channel_service_dto;

import com.sprint.mission.discodeit.dto.user_service_dto.UserDTO;
import lombok.Getter;

@Getter
public class ChannelHostUserUpdateRequestDTO {
    UserDTO oldHostUserDTO;
    UserDTO newHostUserDTO;
    ChannelDTO channelDTO;

    public ChannelHostUserUpdateRequestDTO(UserDTO oldHostUserDTO, UserDTO newHostUserDTO, ChannelDTO channelDTO) {
        this.oldHostUserDTO = oldHostUserDTO;
        this.newHostUserDTO = newHostUserDTO;
        this.channelDTO = channelDTO;
    }
}
