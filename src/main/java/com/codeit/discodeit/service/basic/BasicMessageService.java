package com.codeit.discodeit.service.basic;

import com.codeit.discodeit.dto.message_service_dto.DeleteMessageRequestDto;
import com.codeit.discodeit.dto.message_service_dto.MessageCreateRequest;
import com.codeit.discodeit.dto.message_service_dto.MessageDto;
import com.codeit.discodeit.dto.message_service_dto.MessageUpdateRequest;
import com.codeit.discodeit.dto.response.PageResponse;
import com.codeit.discodeit.dto.response.Pageable;
import com.codeit.discodeit.entity.*;
import com.codeit.discodeit.exception.ErrorCode;
import com.codeit.discodeit.exception.exception.BusinessException;
import com.codeit.discodeit.mapper.BinaryContentMapper;
import com.codeit.discodeit.mapper.MessageMapper;
import com.codeit.discodeit.repository.ChannelRepository;
import com.codeit.discodeit.repository.MessageRepository;
import com.codeit.discodeit.repository.UserRepository;
import com.codeit.discodeit.service.BinaryContentService;
import com.codeit.discodeit.service.ChannelService;
import com.codeit.discodeit.service.MessageService;
import com.codeit.discodeit.service.UserService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {

  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final MessageRepository messageRepository;
  private final BinaryContentService binaryContentService;
  private final MessageMapper messageMapper;
  private final UserService userService;
  private final ChannelService channelService;

  @Override
  @Transactional
  public MessageDto createMessage(MessageCreateRequest messageCreateRequest, List<MultipartFile> attachmentsList) throws IOException {

    User user = userService.findUserByUserId(messageCreateRequest.getAuthorId());
    Channel channel = channelService.findChannelByChannelId(messageCreateRequest.getChannelId());
    List<BinaryContent> binaryContentList = new ArrayList<>();

    for (MultipartFile multipartFile : attachmentsList) {
      binaryContentList.add(BinaryContentMapper.attachmentToBinaryContent(multipartFile));
    }

    Message message = messageMapper.toMessage(messageCreateRequest.getContent(), user, channel, binaryContentList);
    messageRepository.createMessage(message);

    if (message.getAttachments() != null) {
      for (int i = 0 ; i < message.getAttachments().size(); i++) {
        BinaryContent binaryContent = message.getAttachments().get(i);
        binaryContentService.createByteFile(binaryContent, attachmentsList.get(i).getBytes());
        // 데이터를 바이트 파일로 저장
      }
    }

    return messageMapper.toMessageDto(message);
  }

  @Override
  @Transactional
  public void deleteMessage(DeleteMessageRequestDto deleteMessageRequestDTO) {
    Message message = findMessageByMessageId(deleteMessageRequestDTO.getMessageId());
    messageRepository.deleteMessage(message);
  }

  @Override
  @Transactional
  public MessageDto updateMessage(UUID messageId, MessageUpdateRequest messageUpdateRequest) {
    Message message = findMessageByMessageId(messageId);
    message.setContent(messageUpdateRequest.getNewContent());

    messageRepository.updateMessage(message);
    return messageMapper.toMessageDto(message);
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<MessageDto> findMessagesPerPage(UUID channelId, Pageable pageable) {
    List<Message> messageList = messageRepository.findMessagesByChannelId(channelId);


    int page = pageable.getPage();
    int size = pageable.getSize();
    long totalElements = messageList.size();
    int totalPages = (int) Math.ceil((double) totalElements / size);

    int fromIndex = page * size;
    if (fromIndex >= totalElements) {
      return new PageResponse<>(List.of(), page, size, false, totalElements);
    }

    int toIndex = Math.min(fromIndex + size, messageList.size());
    List<MessageDto> pageContent = messageList.subList(fromIndex, toIndex).stream().map(messageMapper::toMessageDto).collect(
        Collectors.toList());
    boolean hasNext = page + 1 < totalPages;

    return new PageResponse<>(pageContent, page, size, hasNext, totalElements);
  }

  @Override
  @Transactional(readOnly = true)
  public Message findMessageByMessageId(UUID messageId) {
    return messageRepository.findMessageByMessageId(messageId)
        .orElseThrow(
            () -> new BusinessException(ErrorCode.NO_FIND_MESSAGE));
  }

  private void findChannelByChannelId(UUID channelId) {
    channelRepository.findChannelByChannelId(channelId)
        .orElseThrow(
            () -> new BusinessException(ErrorCode.NO_FIND_CHANNEL));
  }

  private void findUserByUserId(UUID userId) {
    userRepository.findUserByUserId(userId)
        .orElseThrow(() -> new BusinessException(ErrorCode.NO_FIND_USER));
  }
}