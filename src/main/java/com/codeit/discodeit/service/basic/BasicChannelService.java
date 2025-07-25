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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final ReadStatusService readStatusService;

  @Override
  public Channel createPublicChannel(Channel channel) {

    validateChannelName(channel.getName());
    channelRepository.createChannel(channel);

    List<User> users = userRepository.loadUsers();
    for (User user : users) {
      readStatusService.createReadStatus(user,channel);
    }

    return channel;
  }

  @Override
  public Channel createPrivateChannel(Channel channel, List<UUID> userIdList) {
    channelRepository.createChannel(channel);
    List<User> userList = userRepository.findUserListByUserIdList(userIdList);

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

  @Override
  public Channel findChannelByChannelId(UUID channelId) {
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
