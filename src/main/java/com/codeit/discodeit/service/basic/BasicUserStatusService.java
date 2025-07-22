package com.codeit.discodeit.service.basic;

import com.codeit.discodeit.entity.*;
import com.codeit.discodeit.repository.UserRepository;
import com.codeit.discodeit.repository.UserStatusRepository;
import com.codeit.discodeit.service.UserStatusService;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

  private final UserStatusRepository userStatusRepository;
  private final UserRepository userRepository;


  @Override
  public UserStatus createUserStatus(UUID userId) {
    // JPA 변경된 현재의 시점에서
    // createUserStatus 메소드는 유저 스테이터스는 모종의 이유로 유저와 함께 삭제되는 것이 아닌 단독삭제 되었을 때만 실행됨
    // 정상적인 실행과정에선 실행되지 않는 메소드

    User user = findUserByUserId(userId);
    UserStatus newUserStatus = new UserStatus();
    newUserStatus.setUser(user);
    userStatusRepository.createUserStatus(newUserStatus);

    return newUserStatus;
  }

  @Override
  public UserStatus updateUserStatus(UUID userId, Instant newLastAt) {
    Optional<UserStatus> userStatus = userStatusRepository.findUserStatusByUserId(userId);
    
    if (userStatus.isEmpty()) {
      return createUserStatus(userId);
    }

    UserStatus updateUserStatus = userStatus.get();

    updateUserStatus.setLastActiveAt(newLastAt);
    userStatusRepository.updateUserStatus(updateUserStatus);

    return updateUserStatus;
  }

  private User findUserByUserId(UUID userId) {
    return userRepository.findUserByUserId(userId)
        .orElseThrow(() -> new IllegalStateException("해당하는 유저를 찾을 수 없습니다."));
  }
}
