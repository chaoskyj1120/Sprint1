package com.codeit.discodeit.service.basic;

import com.codeit.discodeit.dto.message_service_dto.DeleteMessageRequestDto;
import com.codeit.discodeit.dto.message_service_dto.MessageCreateRequest;
import com.codeit.discodeit.dto.message_service_dto.MessageUpdateRequestDto;
import com.codeit.discodeit.dto.message_service_dto.PageResponse;
import com.codeit.discodeit.dto.message_service_dto.Pageable;
import com.codeit.discodeit.entity.*;
import com.codeit.discodeit.exception.exception.NoFindChannelException;
import com.codeit.discodeit.exception.exception.NoFindMessageException;
import com.codeit.discodeit.exception.exception.NoFindUserException;
import com.codeit.discodeit.repository.ChannelRepository;
import com.codeit.discodeit.repository.MessageRepository;
import com.codeit.discodeit.repository.UserRepository;
import com.codeit.discodeit.service.MessageService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {

  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final MessageRepository messageRepository;

  @Override
  public Message createMessage(MessageCreateRequest messageCreateRequest,
      List<MultipartFile> attachments) throws IOException {

    User user = findUserByUserId(messageCreateRequest.getAuthorId());
    Channel channel = findChannelByChannelId(messageCreateRequest.getChannelId());
    String contents = messageCreateRequest.getContent();

    Message newMessage = new Message();
    newMessage.setContent(contents);
    newMessage.setAuthor(user);
    newMessage.setChannel(channel);

    List<BinaryContent> binaryContentAttachments = getBinaryContents(attachments);

    newMessage.setAttachments(binaryContentAttachments);
    messageRepository.createMessage(newMessage);

    return newMessage;
  }

  @Override
  public void deleteMessage(DeleteMessageRequestDto deleteMessageRequestDTO) {
    Message message = findMessageByMessageId(deleteMessageRequestDTO.getMessageId());
    messageRepository.deleteMessage(message);
  }

  @Override
  public Message updateMessage(MessageUpdateRequestDto messageUpdateRequestDto) {
    Message message = findMessageByMessageId(messageUpdateRequestDto.getMessageId());
    message.setContent(messageUpdateRequestDto.getNewContent());

    List<BinaryContent> binaryContentAttachments = getBinaryContents(
        messageUpdateRequestDto.getExtraContentsFiles());
    message.setAttachments(binaryContentAttachments);

    messageRepository.updateMessage(message);
    return message;
  }

  private static List<BinaryContent> getBinaryContents(
      List<MultipartFile> attachments) {
    List<BinaryContent> binaryContentAttachments = new ArrayList<>();

    for (MultipartFile attachment : attachments) {
      BinaryContent binaryContent = new BinaryContent();
      binaryContent.setFileName(attachment.getOriginalFilename());
      binaryContent.setContentType(attachment.getContentType());
      binaryContent.setSize(attachment.getSize());
      binaryContentAttachments.add(binaryContent);
    }
    return binaryContentAttachments;
  }

  @Override
  public PageResponse<Message> findMessagesPerPage(UUID channelId, Pageable pageable) {
    List<Message> messageList = messageRepository.findMessagesByChannelId(channelId);

    int size = pageable.getSize();
    int page = pageable.getPage();  // 0-based

    int totalElements = messageList.size();
    int totalPages = (int) Math.ceil((double) totalElements / size);
    boolean hasNext = page + 1 < totalPages;

    int fromIndex = size * page;
    int toIndex = Math.min(fromIndex + size, totalElements);

    messageList = messageList.subList(fromIndex, toIndex);

    if (fromIndex >= totalElements) {
      return new PageResponse<>(List.of(), page, size, false, totalElements);
    }

    return new PageResponse<>(messageList, page, size, hasNext, totalElements);
  }

  @Override
  public Optional<Message> findLastMessageInChannel(UUID channelId){
    return messageRepository.findLastMessageInChannel(channelId);
  }


  private Channel findChannelByChannelId(UUID channelId) {
    return channelRepository.findChannelByChannelId(channelId)
        .orElseThrow(
            () -> new NoFindChannelException("해당하는 채널을 찾을 수 없습니다.", channelId + "can't found"));
  }

  private Message findMessageByMessageId(UUID messageId) {
    return messageRepository.findMessageByMessageId(messageId)
        .orElseThrow(
            () -> new NoFindMessageException("해당하는 메시지를 찾을 수 없습니다.", messageId + "can't found"));
  }

  private User findUserByUserId(UUID userId) {
    return userRepository.findUserByUserId(userId)
        .orElseThrow(() -> new NoFindUserException("해당하는 유저를 찾을 수 없습니다.", userId + "can't found"));
  }
}