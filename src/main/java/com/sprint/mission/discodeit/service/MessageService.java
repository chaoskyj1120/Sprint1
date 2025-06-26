package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel_service_dto.ChannelDTO;
import com.sprint.mission.discodeit.dto.message_service_dto.DeleteMessageRequestDTO;
import com.sprint.mission.discodeit.dto.message_service_dto.MessageCreateRequestDTO;
import com.sprint.mission.discodeit.dto.message_service_dto.MessageDTO;
import com.sprint.mission.discodeit.dto.message_service_dto.MessageUpdateRequestDTO;

import java.util.List;

public interface MessageService {

    MessageDTO createMessage(MessageCreateRequestDTO messageCreateRequestDTO);
    void deleteMessage(DeleteMessageRequestDTO deleteMessageRequestDTO);


    List<MessageDTO> findAllMessage();
    List<MessageDTO> findMessagesByChannelDTO(ChannelDTO channelDTO);
    void updateMessage(MessageUpdateRequestDTO messageUpdateRequestDTO);

}
