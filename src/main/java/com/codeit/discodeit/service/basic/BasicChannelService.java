package com.codeit.discodeit.service.basic;

import com.codeit.discodeit.dto.binary_contents_dto.BinaryContentDto;
import com.codeit.discodeit.dto.channel_service_dto.*;
import com.codeit.discodeit.dto.user_service_dto.UserDto;
import com.codeit.discodeit.entity.*;
import com.codeit.discodeit.exception.exception.DuplicateChannelException;
import com.codeit.discodeit.exception.exception.NoFindChannelException;
import com.codeit.discodeit.exception.exception.NoFindUserException;
import com.codeit.discodeit.repository.ChannelRepository;
import com.codeit.discodeit.repository.MessageRepository;
import com.codeit.discodeit.repository.ReadStatusRepository;
import com.codeit.discodeit.repository.UserRepository;
import com.codeit.discodeit.service.ChannelService;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final MessageRepository messageRepository;
  private final ReadStatusRepository readStatusRepository;

  @Override
  public ChannelDto createPublicChannel(
      CreatePublicChannelRequestDto createPublicChannelRequestDto) {

    validateChannelName(createPublicChannelRequestDto.getName());

    Channel channel = new Channel();
    channel.setName(createPublicChannelRequestDto.getName());
    channel.setDescription(createPublicChannelRequestDto.getDescription());
    channel.setType(ChannelType.PUBLIC);
    channelRepository.createChannel(channel);

    List<User> users = userRepository.loadUsers();
    for (User user : users) {
      ReadStatus readStatus = new ReadStatus();
      readStatus.setChannel(channel);
      readStatus.setUser(user);
      readStatusRepository.createReadStatus(readStatus);
    }

    return getChannelDtoByChannel(channel);
  }

  @Override
  public ChannelDto createPrivateChannel(
      PrivateChannelCreateRequest privateChannelCreateRequest) {

    Channel channel = new Channel();
    channel.setType(ChannelType.PRIVATE);
    channelRepository.createChannel(channel);

    List<UUID> userIdList = privateChannelCreateRequest.getParticipantIds();
    List<User> userList = userRepository.findUserListByUserIdList(userIdList);
    // 시간 복잡도 상 for문에 User를 매번 가져오는 것보다 쿼리문에서 in을 통해 한 번에 가져오는 것이 더 빠름

    for (User user : userList) {
      ReadStatus readStatus = new ReadStatus();
      readStatus.setChannel(channel);
      readStatus.setUser(user);
      readStatusRepository.createReadStatus(readStatus);
    }

    return getChannelDtoByChannel(channel);
  }

  @Override
  public void deleteChannel(UUID channelId) {
    Optional<Channel> channel = channelRepository.findChannelByChannelId(channelId);
    channel.ifPresent(channelRepository::deleteChannel);
  }

  @Override
  public ChannelDto updatePublicChannel(UUID channelId,
      PublicChannelUpdateRequest publicChannelUpdateRequest) {
    Channel channel = findChannelByChannelId(channelId);
    channel.setName(publicChannelUpdateRequest.getNewName());
    channel.setDescription(publicChannelUpdateRequest.getNewDescription());

    channelRepository.updateChannel(channel);

    return getChannelDtoByChannel(channel);
  }

  private Channel findChannelByChannelId(UUID channelId) {
    return channelRepository.findChannelByChannelId(channelId)
        .orElseThrow(() -> new NoFindChannelException("Channel을 찾을 수 없음",
            ("Channel with id {" + channelId + "} not found")));
  }

  private void validateChannelName(String channelName) {
    Optional<Channel> channel = channelRepository.findChannelByChannelName(channelName);
    if (channel.isPresent()) {
      throw new DuplicateChannelException("채널 이름이 중복됩니다.", channelName + "은 이미 사용 중입니다.");
    }
  }

  private UserDto getUserDtoByUser(User user) {
    UserStatus userStatus = user.getStatus();
    Duration duration = Duration.between(userStatus.getLastActiveAt(), Instant.now());
    Boolean loginStatus = duration.toMinutes() < 5;

    BinaryContent binaryContent = user.getProfile();
    BinaryContentDto binaryContentDto = null;

    if (binaryContent != null) {
      binaryContentDto = new BinaryContentDto(
          binaryContent.getId(),
          binaryContent.getFileName(),
          binaryContent.getSize(),
          binaryContent.getContentType()
      );
    }

    return new UserDto(user.getId(), user.getUsername(), user.getEmail(), binaryContentDto, loginStatus);
  }

  private ChannelDto getChannelDtoByChannel(Channel channel) {
    Optional<Message> lastMessageInChannel = messageRepository.findLastMessageInChannel(channel.getId());

    // ReadStatus로 유저의 참가 여부를 확인해야함
    ChannelDto channelDto = new ChannelDto();
    channelDto.setId(channel.getId());
    channelDto.setName(channel.getName());
    channelDto.setDescription(channel.getDescription());
    channelDto.setType(channel.getType());

    lastMessageInChannel.ifPresentOrElse(
        message -> channelDto.setLastMessageAt(message.getCreatedAt()),
        () -> channelDto.setLastMessageAt(null)
    );

    List<ReadStatus> readStatuses = readStatusRepository.findReadStatusByChannel(channel);
    List<UserDto> userDtoList = readStatuses.stream()
        .map(readStatus -> getUserDtoByUser(readStatus.getUser()))
        .toList();

    channelDto.setParticipants(userDtoList);
    return channelDto;
  }

  private void validateUserByUserId(UUID userId) {
    Optional<User> user = userRepository.findUserByUserId(userId);
    if (user.isEmpty()) {
      throw new NoFindUserException("유저를 찾을 수 없습니다.", "Id: " + userId + " 는 없는 유저입니다.");
    }
  }
}
