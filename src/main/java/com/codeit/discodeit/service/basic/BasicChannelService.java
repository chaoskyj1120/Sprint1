package com.codeit.discodeit.service.basic;

import com.codeit.discodeit.dto.channel_service_dto.*;
import com.codeit.discodeit.entity.*;
import com.codeit.discodeit.exception.ErrorCode;
import com.codeit.discodeit.exception.exception.BusinessException;
import com.codeit.discodeit.mapper.ChannelMapper;
import com.codeit.discodeit.mapper.UserMapper;
import com.codeit.discodeit.repository.ChannelRepository;
import com.codeit.discodeit.repository.MessageRepository;
import com.codeit.discodeit.repository.UserRepository;
import com.codeit.discodeit.service.ChannelService;
import com.codeit.discodeit.service.ReadStatusService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final ReadStatusService readStatusService;
  private final ChannelMapper channelMapper;
  private final MessageRepository messageRepository;
  private final UserMapper userMapper;

  @Override
  @Transactional
  public ChannelDto createPublicChannel(CreatePublicChannelRequestDto createPublicChannelRequestDto) {
    log.info("[createPublicChannel] 요청 수신");

    Channel channel = channelMapper.toPublicChannel(createPublicChannelRequestDto);

    validateChannelNameDuplicated(channel.getName());
    channelRepository.createChannel(channel);

    List<User> users = userRepository.loadUsers();
    for (User user : users) {
      readStatusService.createReadStatus(user, channel);
    }

    ChannelDto result = toChannelDto(channel);
    log.info("[createPublicChannel] 채널 생성 완료: channelId={}", result.getId());
    return result;
  }

  @Override
  @Transactional
  public ChannelDto createPrivateChannel(List<UUID> userIdList) {
    log.info("[createPrivateChannel] 요청 수신: userIds={}", userIdList);

    Channel channel = channelMapper.toPrivateChannel();
    channelRepository.createChannel(channel);

    List<User> userList = userRepository.findUserListByUserIdList(userIdList);
    for (User user : userList) {
      readStatusService.createReadStatus(user, channel);
    }

    ChannelDto result = toChannelDto(channel);
    log.info("[createPrivateChannel] 채널 생성 완료: channelId={}", result.getId());
    return result;
  }

  @Override
  @Transactional
  public void deleteChannel(UUID channelId) {
    log.info("[deleteChannel] 요청 수신: channelId={}", channelId);
    Optional<Channel> channel = channelRepository.findChannelByChannelId(channelId);
    if (channel.isPresent()) {
      channelRepository.deleteChannel(channel.get());
      log.info("[deleteChannel] 채널 삭제 완료: channelId={}", channelId);
    } else {
      log.debug("[deleteChannel] 삭제 실패 - 채널을 찾을 수 없음: channelId={}", channelId);
    }
  }

  @Override
  @Transactional
  public ChannelDto updatePublicChannel(UUID channelId, PublicChannelUpdateRequest publicChannelUpdateRequest) {
    log.info("[updatePublicChannel] 요청 수신: channelId={}, newName={}, newDescription={}",
        channelId, publicChannelUpdateRequest.getNewName(), publicChannelUpdateRequest.getNewDescription());

    Channel channel = findChannelByChannelId(channelId);
    channel.setName(publicChannelUpdateRequest.getNewName());
    channel.setDescription(publicChannelUpdateRequest.getNewDescription());

    channelRepository.updateChannel(channel);

    ChannelDto result = toChannelDto(channel);
    log.info("[updatePublicChannel] 채널 수정 완료: channelId={}", result.getId());
    return result;
  }

  @Override
  @Transactional(readOnly = true)
  public List<ChannelDto> findChannelListByUserId(UUID userId){
    List<ReadStatus> readStatusList = readStatusService.findReadStatusesByUserId(userId);
    List<Channel> channelList = readStatusList.stream().map(ReadStatus::getChannel).toList();
    return channelList.stream().map(this::toChannelDto).collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public Channel findChannelByChannelId(UUID channelId) {
    return channelRepository.findChannelByChannelId(channelId)
        .orElseThrow(() -> new BusinessException(ErrorCode.NO_FIND_CHANNEL));
  }

  private void validateChannelNameDuplicated(String channelName) {
    log.info("[validateChannelNameDuplicated] 채널 이름 중복 확인 시작: channelName={}", channelName);
    Optional<Channel> channel = channelRepository.findChannelByChannelName(channelName);
    if (channel.isPresent()) {
      log.debug("[validateChannelNameDuplicated] 채널 이름 중복 발견: channelName={}", channelName);
      throw new BusinessException(ErrorCode.DUPLICATE_CHANNEL);
    }
    log.info("[validateChannelNameDuplicated] 채널 이름 중복 확인 종료: channelName={}", channelName);
  }

  private Optional<Message> findLastMessageInChannel(UUID channelId){
    return messageRepository.findLastMessageInChannel(channelId);
  }

  private ChannelDto toChannelDto(Channel channel) {
    Optional<Message> lastMessage = findLastMessageInChannel(channel.getId());
    List<ReadStatus> readStatuses = readStatusService.findReadStatusesByChannelId(channel);
    return channelMapper.toChannelDto(channel, lastMessage, readStatuses, userMapper);
  }
}
