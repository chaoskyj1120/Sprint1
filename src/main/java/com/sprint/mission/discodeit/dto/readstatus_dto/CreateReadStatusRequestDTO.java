package com.sprint.mission.discodeit.dto.readstatus_dto;

import com.sprint.mission.discodeit.dto.channel_service_dto.ChannelDTO;
import com.sprint.mission.discodeit.dto.user_service_dto.UserDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateReadStatusRequestDTO {
    private final UserDTO userDTO;
    private final ChannelDTO channelDTO;
    private UUID messageId;

    public CreateReadStatusRequestDTO(UserDTO userDTO, ChannelDTO channelDTO) {
        this.userDTO = userDTO;
        this.channelDTO = channelDTO;
        this.messageId = channelDTO.getLastMessageId();
    }
}
