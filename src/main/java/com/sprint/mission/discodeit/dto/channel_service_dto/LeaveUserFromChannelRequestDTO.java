package com.sprint.mission.discodeit.dto.channel_service_dto;

import com.sprint.mission.discodeit.dto.user_service_dto.UserDTO;
import lombok.Getter;

@Getter
public class LeaveUserFromChannelRequestDTO {
    UserDTO userDTO;
    ChannelDTO channelDTO;

    public LeaveUserFromChannelRequestDTO(UserDTO userDTO, ChannelDTO channelDTO) {
        this.userDTO = userDTO;
        this.channelDTO = channelDTO;
    }
}
