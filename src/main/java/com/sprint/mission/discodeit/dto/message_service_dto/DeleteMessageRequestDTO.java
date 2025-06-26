package com.sprint.mission.discodeit.dto.message_service_dto;

import com.sprint.mission.discodeit.dto.user_service_dto.UserDTO;
import lombok.Getter;

@Getter
public class DeleteMessageRequestDTO {
    UserDTO userDTO;
    MessageDTO messageDTO;

    public DeleteMessageRequestDTO(UserDTO userDTO, MessageDTO messageDTO) {
        this.userDTO = userDTO;
        this.messageDTO = messageDTO;
    }
}
