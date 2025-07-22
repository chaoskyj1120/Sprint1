package com.codeit.discodeit.service;

import com.codeit.discodeit.dto.message_service_dto.DeleteMessageRequestDto;
import com.codeit.discodeit.dto.message_service_dto.MessageCreateRequest;
import com.codeit.discodeit.dto.message_service_dto.MessageDto;
import com.codeit.discodeit.dto.message_service_dto.MessageUpdateRequestDto;
import com.codeit.discodeit.dto.message_service_dto.PageResponse;
import com.codeit.discodeit.dto.message_service_dto.Pageable;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface MessageService {

  MessageDto createMessage(MessageCreateRequest messageCreateRequest,
      List<MultipartFile> attachments) throws IOException;

  void deleteMessage(DeleteMessageRequestDto deleteMessageRequestDTO);

  MessageDto updateMessage(MessageUpdateRequestDto messageUpdateRequestDto);

  PageResponse<MessageDto> findMessagesPerPage(UUID channelId, Pageable pageable);

}
