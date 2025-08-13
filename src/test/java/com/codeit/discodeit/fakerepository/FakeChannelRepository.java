package com.codeit.discodeit.fakerepository;

import com.codeit.discodeit.entity.Channel;
import com.codeit.discodeit.entity.ChannelType;
import com.codeit.discodeit.repository.ChannelRepository;

import java.util.*;

public class FakeChannelRepository implements ChannelRepository {

  private final Map<UUID, Channel> store = new HashMap<>();

  @Override
  public void createChannel(Channel channel) {
    if (channel == null || channel.getId() == null) {
      throw new IllegalArgumentException("Channel 또는 ID가 null일 수 없습니다.");
    }
    store.put(channel.getId(), channel);
  }

  @Override
  public void deleteChannel(Channel channel) {
    if (channel != null) {
      store.remove(channel.getId());
    }
  }

  @Override
  public void updateChannel(Channel channel) {
    if (channel != null && store.containsKey(channel.getId())) {
      store.put(channel.getId(), channel);
    }
  }

  @Override
  public List<Channel> loadChannels() {
    return new ArrayList<>(store.values());
  }

  @Override
  public List<Channel> findAllPublicChannel() {
    List<Channel> publicChannels = new ArrayList<>();
    for (Channel channel : store.values()) {
      if (channel.getType().equals(ChannelType.PUBLIC)) { // Channel에 isPublic 필드가 있다고 가정
        publicChannels.add(channel);
      }
    }
    return publicChannels;
  }

  @Override
  public Optional<Channel> findChannelByChannelName(String channelName) {
    for (Channel channel : store.values()) {
      if (channel.getName().equals(channelName)) { // Channel에 getName() 있다고 가정
        return Optional.of(channel);
      }
    }
    return Optional.empty();
  }

  @Override
  public Optional<Channel> findChannelByChannelId(UUID channelId) {
    return Optional.ofNullable(store.get(channelId));
  }

}
