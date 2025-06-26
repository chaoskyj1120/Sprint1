package com.sprint.mission.discodeit.dto.binary_contents_dto;

import com.sprint.mission.discodeit.dto.message_service_dto.MessageDTO;
import com.sprint.mission.discodeit.dto.user_service_dto.UserDTO;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindBinaryContentRequestDTO {
    private final UUID referenceId;

    public FindBinaryContentRequestDTO(UserDTO userDTO) {
        this.referenceId = userDTO.getUserId();
    }
    public FindBinaryContentRequestDTO(MessageDTO messageDTO) {
        this.referenceId = messageDTO.getMessageId();
    }
}
