package com.codeit.discodeit.fakerepository;

import com.codeit.discodeit.entity.UserStatus;
import com.codeit.discodeit.repository.UserStatusRepository;

import java.util.*;
public class FakeUserStatusRepository implements UserStatusRepository {

  private final Map<UUID, UserStatus> store = new HashMap<>();

  @Override
  public void createUserStatus(UserStatus userStatus) {
    if (userStatus == null || userStatus.getId() == null) {
      throw new IllegalArgumentException("UserStatus 또는 ID가 null일 수 없습니다.");
    }
    store.put(userStatus.getId(), userStatus);
  }

  @Override
  public void updateUserStatus(UserStatus userStatus) {
    if (userStatus != null && store.containsKey(userStatus.getId())) {
      store.put(userStatus.getId(), userStatus);
    }
  }

  @Override
  public Optional<UserStatus> findUserStatusByUserStatusId(UUID userStatusId) {
    return Optional.ofNullable(store.get(userStatusId));
  }

  @Override
  public Optional<UserStatus> findUserStatusByUserId(UUID userId) {
    return store.values().stream()
        .filter(us -> us.getUser() != null && us.getUser().getId().equals(userId))
        .findFirst();
  }

  // 테스트용 전체 상태 확인
  public List<UserStatus> loadAll() {
    return new ArrayList<>(store.values());
  }
}
