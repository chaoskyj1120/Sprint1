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
    User user = findUserByUserId(userId);
    UserStatus newUserStatus = new UserStatus();
    newUserStatus.setUser(user);
    //userStatusRepository.createUserStatus(newUserStatus); // user 연결되어서 대신 생성

    return newUserStatus;
  }

  @Override
  public UserStatus updateUserStatus(UUID userId, Instant newLastAt) {
    Optional<UserStatus> userStatus = userStatusRepository.findUserStatusByUserId(userId);
    
    if (userStatus.isEmpty()) {
      UserStatus newUserStatus = createUserStatus(userId);
      userStatusRepository.createUserStatus(newUserStatus);
      return newUserStatus; // 모종의 이유로 유저는 존재하지만 userStatus가 없을 때
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
