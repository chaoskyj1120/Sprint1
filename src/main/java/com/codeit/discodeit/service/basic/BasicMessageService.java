package com.codeit.discodeit.service.basic;

import com.codeit.discodeit.dto.binary_contents_dto.BinaryContentDto;
import com.codeit.discodeit.dto.message_service_dto.DeleteMessageRequestDto;
import com.codeit.discodeit.dto.message_service_dto.MessageCreateRequest;
import com.codeit.discodeit.dto.message_service_dto.MessageDto;
import com.codeit.discodeit.dto.message_service_dto.MessageUpdateRequestDto;
import com.codeit.discodeit.dto.message_service_dto.PageResponse;
import com.codeit.discodeit.dto.message_service_dto.Pageable;
import com.codeit.discodeit.dto.user_service_dto.UserDto;
import com.codeit.discodeit.entity.*;
import com.codeit.discodeit.exception.exception.NoFindChannelException;
import com.codeit.discodeit.exception.exception.NoFindMessageException;
import com.codeit.discodeit.exception.exception.NoFindUserException;
import com.codeit.discodeit.repository.ChannelRepository;
import com.codeit.discodeit.repository.MessageRepository;
import com.codeit.discodeit.repository.UserRepository;
import com.codeit.discodeit.service.MessageService;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
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
  public MessageDto createMessage(MessageCreateRequest messageCreateRequest,
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

    return getMessageDtoByMessage(newMessage);
  }

  @Override
  public void deleteMessage(DeleteMessageRequestDto deleteMessageRequestDTO) {
    Message message = findMessageByMessageId(deleteMessageRequestDTO.getMessageId());
    messageRepository.deleteMessage(message);
  }

  @Override
  public MessageDto updateMessage(MessageUpdateRequestDto messageUpdateRequestDto) {
    Message message = findMessageByMessageId(messageUpdateRequestDto.getMessageId());

    User user = message.getAuthor();
    message.setContent(messageUpdateRequestDto.getNewContent());

    List<BinaryContent> binaryContentAttachments = getBinaryContents(
        messageUpdateRequestDto.getExtraContentsFiles());
    message.setAttachments(binaryContentAttachments);

    messageRepository.updateMessage(message);
    return getMessageDtoByMessage(message);
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
  public PageResponse<MessageDto> findMessagesPerPage(UUID channelId, Pageable pageable) {
    List<Message> messageList = messageRepository.findMessagesByChannelId(channelId);

    int size = pageable.getSize();
    int page = pageable.getPage();  // 0-based
    int totalElements = messageList.size();
    int totalPages = (int) Math.ceil((double) totalElements / size);
    boolean hasNext = page + 1 < totalPages;

    int fromIndex = size * page;
    int toIndex = Math.min(fromIndex + size, totalElements);

    if (fromIndex >= totalElements) {
      return new PageResponse<>(List.of(), page, size, false, totalElements);
    }

    List<MessageDto> messageDtoList = messageList.subList(fromIndex, toIndex)
        .stream().map(this::getMessageDtoByMessage).toList();

    return new PageResponse<>(messageDtoList, page, size, hasNext, totalElements);
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

  private MessageDto getMessageDtoByMessage(Message message) {
    UserDto userDto = getUserDtoByUser(message.getAuthor());
    List<BinaryContent> binaryContentList = message.getAttachments();
    List<BinaryContentDto> binaryContentDtoList = binaryContentList.stream()
        .map(this::getBinaryContetnDtoByBinaryContent)
        .collect(Collectors.toList());

    return new MessageDto(message.getId(), message.getCreatedAt(), message.getUpdatedAt(),
        message.getContent(), message.getChannel().getId(), userDto, binaryContentDtoList);
  }

  private UserDto getUserDtoByUser(User user) {
    UserStatus userStatus = user.getStatus();
    Duration duration = Duration.between(userStatus.getLastActiveAt(), Instant.now());
    Boolean loginStatus = duration.toMinutes() < 5;

    return new UserDto(user.getId(), user.getUsername(), user.getEmail(), getBinaryContetnDtoByBinaryContent(user.getProfile()), loginStatus);
  }

  private BinaryContentDto getBinaryContetnDtoByBinaryContent(BinaryContent binaryContent) {
    return new BinaryContentDto(binaryContent.getId(), binaryContent.getFileName(), binaryContent.getSize(), binaryContent.getContentType());
  }
}