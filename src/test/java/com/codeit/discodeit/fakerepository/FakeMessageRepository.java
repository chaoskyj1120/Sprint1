package com.codeit.discodeit.fakerepository;

import com.codeit.discodeit.entity.Message;
import com.codeit.discodeit.repository.MessageRepository;

import java.util.*;
import java.util.stream.Collectors;

public class FakeMessageRepository implements MessageRepository {

  private final Map<UUID, Message> store = new HashMap<>();

  @Override
  public void createMessage(Message message) {
    if (message == null || message.getId() == null) {
      throw new IllegalArgumentException("Message 또는 ID가 null일 수 없습니다.");
    }
    store.put(message.getId(), message);
  }

  @Override
  public void deleteMessage(Message message) {
    if (message != null) {
      store.remove(message.getId());
    }
  }

  @Override
  public void updateMessage(Message message) {
    if (message != null && store.containsKey(message.getId())) {
      store.put(message.getId(), message);
    }
  }

  @Override
  public Optional<Message> findMessageByMessageId(UUID messageId) {
    return Optional.ofNullable(store.get(messageId));
  }

  @Override
  public List<Message> findMessagesByChannelId(UUID channelId) {
    return store.values().stream()
        .filter(m -> m.getChannel() != null && m.getChannel().getId().equals(channelId))
        .sorted(Comparator.comparing(Message::getCreatedAt))
        .collect(Collectors.toList());
  }

  @Override
  public Optional<Message> findLastMessageInChannel(UUID channelId) {
    return store.values().stream()
        .filter(m -> m.getChannel() != null && m.getChannel().getId().equals(channelId))
        .max(Comparator.comparing(Message::getCreatedAt));
  }

  // 테스트 확인용: 전체 저장소 반환
  public List<Message> loadAll() {
    return new ArrayList<>(store.values());
  }
}
