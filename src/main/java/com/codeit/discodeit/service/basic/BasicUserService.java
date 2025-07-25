package com.codeit.discodeit.service.basic;

import com.codeit.discodeit.dto.user_service_dto.*;
import com.codeit.discodeit.entity.*;
import com.codeit.discodeit.exception.exception.DuplicateUserException;
import com.codeit.discodeit.exception.exception.NoFindUserException;
import com.codeit.discodeit.repository.ChannelRepository;
import com.codeit.discodeit.repository.UserRepository;
import com.codeit.discodeit.repository.UserStatusRepository;
import com.codeit.discodeit.service.BinaryContentService;
import com.codeit.discodeit.service.ReadStatusService;
import com.codeit.discodeit.service.UserService;
import com.codeit.discodeit.service.UserStatusService;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Transactional
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final ReadStatusService readStatusService;
  private final UserStatusService userStatusService;
  private final BinaryContentService binaryContentService;
  private final UserStatusRepository userStatusRepository;

  @Override
  public User createUser(User user, byte[] profileImgBytes) {

    validateUserNameNotDuplicated(user.getUsername());
    validateUserEmailNotDuplicated(user.getEmail());

    userRepository.createUser(user);
    binaryContentService.createByteFile(user.getProfile(), profileImgBytes);

    UserStatus userStatus = userStatusService.createUserStatus(user);
    user.setStatus(userStatus);
    userStatusRepository.createUserStatus(userStatus);

    // 신규 유저 생성시 공용 채널의 readStatus 자동으로 추가
    List<Channel> channels = channelRepository.findAllPublicChannel();
    for (Channel channel : channels) {
      readStatusService.createReadStatus(user, channel);
    }

    return user;
  }

  @Override
  public User updateUser(UserUpdateRequest userUpdateRequest, BinaryContent newProfileImage, byte[] profileImgBytes)
      throws IOException {
    User targetUser = findUserByUserId(userUpdateRequest.getUserId());

    // 사용자 기본 정보 수정
    if (userUpdateRequest.getNewEmail() != null && !userUpdateRequest.getNewEmail()
        .equals(targetUser.getEmail())) {
      validateUserEmailNotDuplicated(userUpdateRequest.getNewEmail());
      targetUser.setEmail(userUpdateRequest.getNewEmail());
    }

    if (userUpdateRequest.getNewUsername() != null && !userUpdateRequest.getNewUsername()
        .equals(targetUser.getUsername())) {
      validateUserNameNotDuplicated(userUpdateRequest.getNewUsername());
      targetUser.setUsername(userUpdateRequest.getNewUsername());
    }

    if (userUpdateRequest.getNewPassword() != null && !userUpdateRequest.getNewPassword()
        .equals(targetUser.getPassword())) {
      targetUser.setPassword(userUpdateRequest.getNewPassword());
    }

    if (newProfileImage != null) {
      BinaryContent oldProfileImg = targetUser.getProfile();

      boolean isSameImage = oldProfileImg != null
          && oldProfileImg.getSize() == newProfileImage.getSize()
          && oldProfileImg.getContentType().equals(newProfileImage.getContentType())
          && oldProfileImg.getFileName().equals(newProfileImage.getFileName());

      if (!isSameImage) {
        targetUser.setProfile(newProfileImage);
        userRepository.updateUser(targetUser);
        binaryContentService.createByteFile(targetUser.getProfile(), profileImgBytes);
      }
    }
    userRepository.updateUser(targetUser);
    return targetUser;
  }

  @Override
  public void deleteUser(UUID userId) {
    User user = findUserByUserId(userId);
    userRepository.deleteUser(user);
  }

  @Override
  public List<User> findAllUser() {
    return userRepository.loadUsers();
  }

  @Override
  public User findUserByUserId(UUID userId) {
    Optional<User> user = userRepository.findUserByUserId(userId);
    if (user.isEmpty()) {
      throw new NoFindUserException("유저를 찾을 수 없습니다.", "Id: " + userId + " 는 없는 유저입니다.");
    }
    return user.get();
  }

  private void validateUserNameNotDuplicated(String userName) {
    if (userRepository.findUserByUserName(userName).isPresent()) {
      throw new DuplicateUserException("같은 email 또는 username를 사용하는 User가 이미 존재함",
          (userName + "은 이미 있는 이름입니다."));
    }
  }

  private void validateUserEmailNotDuplicated(String userEmail) {
    if (userRepository.findUserByUserName(userEmail).isPresent()) {
      throw new DuplicateUserException("같은 email 또는 username를 사용하는 User가 이미 존재함",
          (userEmail + "은 이미 있는 이름입니다."));
    }
  }

}
