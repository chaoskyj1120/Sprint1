package com.sprint.mission.discodeit.dto.message_service_dto;

import com.sprint.mission.discodeit.dto.channel_service_dto.ChannelDTO;
import com.sprint.mission.discodeit.dto.user_service_dto.UserDTO;
import lombok.Getter;

import java.util.List;

@Getter
public class MessageCreateRequestDTO {
    private final UserDTO userDTO;
    private final ChannelDTO channelDTO;
    private final String messageContents;
    private final List<String> extraContentsFilePath;

    public MessageCreateRequestDTO(UserDTO userDTO, ChannelDTO channelDTO, String messageContents, List<String> extraContentsFilePath) {
        this.userDTO = userDTO;
        this.channelDTO = channelDTO;
        this.messageContents = messageContents;
        this.extraContentsFilePath = extraContentsFilePath;
    }

    public MessageCreateRequestDTO(UserDTO userDTO, ChannelDTO channelDTO, String messageContents) {
        this.userDTO = userDTO;
        this.channelDTO = channelDTO;
        this.messageContents = messageContents;
        this.extraContentsFilePath = null;
    }
}
