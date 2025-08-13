package com.codeit.discodeit.fakerepository;
import com.codeit.discodeit.entity.Channel;
import com.codeit.discodeit.entity.ReadStatus;
import com.codeit.discodeit.repository.ReadStatusRepository;

import java.util.*;
import java.util.stream.Collectors;

public class FakeReadStatusRepository implements ReadStatusRepository {

  private final Map<UUID, ReadStatus> store = new HashMap<>();

  @Override
  public void createReadStatus(ReadStatus readStatus) {
    if (readStatus == null || readStatus.getId() == null) {
      throw new IllegalArgumentException("ReadStatus 또는 ID가 null일 수 없습니다.");
    }
    store.put(readStatus.getId(), readStatus);
  }

  @Override
  public void updateReadStatus(ReadStatus readStatus) {
    if (readStatus != null && store.containsKey(readStatus.getId())) {
      store.put(readStatus.getId(), readStatus);
    }
  }

  @Override
  public Optional<ReadStatus> findReadStatusesByReadStatusId(UUID readStatusId) {
    return Optional.ofNullable(store.get(readStatusId));
  }

  @Override
  public Optional<ReadStatus> findReadStatusesByUserIdAndChannelId(UUID userId, UUID channelId) {
    return store.values().stream()
        .filter(rs -> rs.getUser() != null && rs.getUser().getId().equals(userId)
            && rs.getChannel() != null && rs.getChannel().getId().equals(channelId))
        .findFirst();
  }

  @Override
  public List<ReadStatus> findReadStatusesByUserId(UUID userId) {
    return store.values().stream()
        .filter(rs -> rs.getUser() != null && rs.getUser().getId().equals(userId))
        .collect(Collectors.toList());
  }

  @Override
  public List<ReadStatus> findReadStatusByChannel(Channel channel) {
    return store.values().stream()
        .filter(rs -> rs.getChannel() != null && rs.getChannel().equals(channel))
        .collect(Collectors.toList());
  }

  // 테스트 확인용: 전체 저장소 반환
  public List<ReadStatus> loadAll() {
    return new ArrayList<>(store.values());
  }
}
