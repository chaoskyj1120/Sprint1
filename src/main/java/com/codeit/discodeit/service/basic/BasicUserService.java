package com.codeit.discodeit.service.basic;

import com.codeit.discodeit.dto.user_service_dto.*;
import com.codeit.discodeit.entity.*;
import com.codeit.discodeit.exception.exception.DuplicateUserException;
import com.codeit.discodeit.exception.exception.NoFindUserException;
import com.codeit.discodeit.repository.ChannelRepository;
import com.codeit.discodeit.repository.UserRepository;
import com.codeit.discodeit.service.ReadStatusService;
import com.codeit.discodeit.service.UserService;
import com.codeit.discodeit.service.UserStatusService;
import jakarta.transaction.Transactional;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
@Transactional
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final ReadStatusService readStatusService;
  private final UserStatusService userStatusService;

  @Override
  public User createUser(UserCreateRequest userCreateRequest) throws IOException {

    validateUserNameNotDuplicated(userCreateRequest.getUsername());
    validateUserEmailNotDuplicated(userCreateRequest.getEmail());

    User user = toUser(userCreateRequest);
    userRepository.createUser(user);

    UserStatus userStatus = userStatusService.createUserStatus(user.getId());
    user.setStatus(userStatus);

    // 신규 유저 생성시 공용 채널의 readStatus 자동으로 추가
    List<Channel> channels = channelRepository.findAllPublicChannel();
    for (Channel channel : channels) {
      readStatusService.createReadStatus(user, channel);
    }

    return user;
  }

  @Override
  public User updateUser(UserUpdateRequest userUpdateRequest, MultipartFile profileImage)
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

    if (profileImage != null && !profileImage.isEmpty()) {
      BinaryContent newProfileImg = MulitpartFiletoBinaryContent(profileImage);
      BinaryContent currentProfile = targetUser.getProfile();

      boolean isSameImage = currentProfile != null
          && currentProfile.getSize() == newProfileImg.getSize()
          && currentProfile.getContentType().equals(newProfileImg.getContentType())
          && currentProfile.getFileName().equals(newProfileImg.getFileName());

      if (!isSameImage) {
        targetUser.setProfile(newProfileImg);
      }
    }
    // DB에 사용자 정보 반영
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

  private BinaryContent MulitpartFiletoBinaryContent(MultipartFile profileImg) throws IOException {

    BinaryContent binaryContent = new BinaryContent();

    if (profileImg == null || profileImg.isEmpty()) {
      File file = new File("src/main/resources/profileImg/basicUserProfileImage.png");
      if (!file.exists()) {
        throw new FileNotFoundException("기본 프로필 이미지가 존재하지 않습니다: " + file.getAbsolutePath());
      }

      String contentType = Files.probeContentType(file.toPath());
      if (contentType == null) {
        contentType = "application/octet-stream";
      }
      binaryContent.setSize(file.length());
      binaryContent.setContentType(contentType);
      binaryContent.setBytes(Files.readAllBytes(file.toPath()));
      binaryContent.setFileName(file.getName());
      // 이미지를 선택하지 않았을 때 기본 이미지 설정
    }
    else {
      binaryContent.setSize(profileImg.getSize());
      binaryContent.setContentType(profileImg.getContentType());
      binaryContent.setBytes(profileImg.getBytes());
      binaryContent.setFileName(profileImg.getOriginalFilename());
    }

    return binaryContent;
  }

  private User toUser(UserCreateRequest userCreateRequest) throws IOException {

    BinaryContent profileImageBinaryContent = MulitpartFiletoBinaryContent(userCreateRequest.getProfileImage());

    String rawPassword = userCreateRequest.getPassword(); // 암호화 추후 구현
    String userName = userCreateRequest.getUsername();
    String userEmail = userCreateRequest.getEmail();

    User user = new User();
    user.setUsername(userName);
    user.setPassword(rawPassword);
    user.setEmail(userEmail);
    user.setProfile(profileImageBinaryContent);

    return user;
  }
}
