package com.codeit.discodeit.service;

import com.codeit.discodeit.dto.message_service_dto.DeleteMessageRequestDto;
import com.codeit.discodeit.dto.message_service_dto.MessageCreateRequest;
import com.codeit.discodeit.dto.message_service_dto.MessageUpdateRequestDto;
import com.codeit.discodeit.dto.message_service_dto.PageResponse;
import com.codeit.discodeit.dto.message_service_dto.Pageable;
import com.codeit.discodeit.entity.Message;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface MessageService {

  Message createMessage(MessageCreateRequest messageCreateRequest,
      List<MultipartFile> attachments) throws IOException;

  void deleteMessage(DeleteMessageRequestDto deleteMessageRequestDTO);

  Message updateMessage(MessageUpdateRequestDto messageUpdateRequestDto);

  PageResponse<Message> findMessagesPerPage(UUID channelId, Pageable pageable);

  Optional<Message> findLastMessageInChannel(UUID channelId);
}
