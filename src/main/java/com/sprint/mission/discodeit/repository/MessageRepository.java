package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.ArrayList;

public interface MessageRepository {
    void saveMessages();
    ArrayList<Message> loadMessages();
}
