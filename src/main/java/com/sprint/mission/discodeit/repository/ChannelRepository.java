package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface ChannelRepository {

    void createChannel(Channel channel);
    void deleteChannel(Channel channel);
    void addUserToChannel(User user, Channel channelName);
    void leaveUserFromChannel(User user, Channel channel);
    void updateChannelName(User user, Channel channel, String newName);
    void updateHostUser(User oldHostUser, Channel channel, User newHostUser);

    void saveChannels(List<Channel> channels);
    List<Channel> loadChannels();

    Channel getChannelById(UUID channelId);
}