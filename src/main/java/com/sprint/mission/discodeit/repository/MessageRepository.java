package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface MessageRepository {
    void createMessage(Message message);
    void deleteMessage(User user, Message message);
    void updateMessage(User user, Message message, String newContents);

    List<Message> loadMessages();
    void saveMessages(List<Message> messages);

    Message getMessageById(UUID id);
}