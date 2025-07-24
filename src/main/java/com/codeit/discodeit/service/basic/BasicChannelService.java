package com.codeit.discodeit.service.basic;

import com.codeit.discodeit.dto.channel_service_dto.*;
import com.codeit.discodeit.entity.*;
import com.codeit.discodeit.exception.exception.DuplicateChannelException;
import com.codeit.discodeit.exception.exception.NoFindChannelException;
import com.codeit.discodeit.repository.ChannelRepository;
import com.codeit.discodeit.repository.UserRepository;
import com.codeit.discodeit.service.ChannelService;
import com.codeit.discodeit.service.ReadStatusService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.Locked.Read;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final ReadStatusService readStatusService;

  @Override
  public Channel createPublicChannel(
      CreatePublicChannelRequestDto createPublicChannelRequestDto) {

    validateChannelName(createPublicChannelRequestDto.getName());

    Channel channel = new Channel();
    channel.setName(createPublicChannelRequestDto.getName());
    channel.setDescription(createPublicChannelRequestDto.getDescription());
    channel.setType(ChannelType.PUBLIC);
    channelRepository.createChannel(channel);

    List<User> users = userRepository.loadUsers();
    for (User user : users) {
      readStatusService.createReadStatus(user,channel);
    }

    return channel;
  }

  @Override
  public Channel createPrivateChannel(
      PrivateChannelCreateRequest privateChannelCreateRequest) {

    Channel channel = new Channel();
    channel.setType(ChannelType.PRIVATE);
    channelRepository.createChannel(channel);

    List<UUID> userIdList = privateChannelCreateRequest.getParticipantIds();
    List<User> userList = userRepository.findUserListByUserIdList(userIdList);
    // 시간 복잡도 상 for문에 User를 매번 가져오는 것보다 쿼리문에서 in을 통해 한 번에 가져오는 것이 더 빠름

    for (User user : userList) {
      readStatusService.createReadStatus(user,channel);
    }

    return channel;
  }

  @Override
  public void deleteChannel(UUID channelId) {
    Optional<Channel> channel = channelRepository.findChannelByChannelId(channelId);
    channel.ifPresent(channelRepository::deleteChannel);
  }

  @Override
  public Channel updatePublicChannel(UUID channelId,
      PublicChannelUpdateRequest publicChannelUpdateRequest) {
    Channel channel = findChannelByChannelId(channelId);
    channel.setName(publicChannelUpdateRequest.getNewName());
    channel.setDescription(publicChannelUpdateRequest.getNewDescription());

    channelRepository.updateChannel(channel);
    return channel;
  }

  @Override
  public List<Channel> findChannelListByUserId(UUID userId){
    List<ReadStatus> readStatusList = readStatusService.findReadStatusesByUserId(userId);
    return readStatusList.stream().map(ReadStatus::getChannel)
        .toList();
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

}
