package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.ArrayList;

public interface ChannelRepository {
    void saveChannels();
    ArrayList<Channel> loadChannels();
}
