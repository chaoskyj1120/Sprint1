package com.sprint.mission.discodeit.dto.message_service_dto;

import com.sprint.mission.discodeit.dto.user_service_dto.UserDTO;
import lombok.Getter;

import java.util.List;

@Getter
public class MessageUpdateRequestDTO {
    UserDTO userDTO;
    MessageDTO messageDTO;
    String newContent;
    List<String> extraContentsFilePath;

    public MessageUpdateRequestDTO(UserDTO userDTO, MessageDTO messageDTO, String newContent, List<String> extraContentsFilePath) {
        this.userDTO = userDTO;
        this.messageDTO = messageDTO;
        this.newContent = newContent;
        this.extraContentsFilePath = extraContentsFilePath;
    }
}
