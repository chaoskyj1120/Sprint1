package com.codeit.discodeit.service.basic;

import com.codeit.discodeit.dto.binary_contents_dto.BinaryContentDto;
import com.codeit.discodeit.dto.user_service_dto.*;
import com.codeit.discodeit.entity.*;
import com.codeit.discodeit.exception.exception.DuplicateUserException;
import com.codeit.discodeit.exception.exception.NoFindUserException;
import com.codeit.discodeit.repository.ChannelRepository;
import com.codeit.discodeit.repository.ReadStatusRepository;
import com.codeit.discodeit.repository.UserRepository;
import com.codeit.discodeit.service.UserService;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
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
  private final ReadStatusRepository readStatusRepository;

  @Override
  public UserDto createUser(UserCreateRequest userCreateRequest) throws IOException {

    BinaryContent profileImg = new BinaryContent();
    profileImg.setSize(userCreateRequest.getProfileImage().getSize());
    profileImg.setContentType(userCreateRequest.getProfileImage().getContentType());
    profileImg.setBytes(userCreateRequest.getProfileImage().getBytes());
    profileImg.setFileName(userCreateRequest.getProfileImage().getName());

    String userName = userCreateRequest.getUsername();
    String userEmail = userCreateRequest.getEmail();
    String rawPassword = userCreateRequest.getPassword(); // 암호화 추후 구현

    validateUserNameNotDuplicated(userName);
    validateUserEmailNotDuplicated(userEmail);

    User user = new User();
    user.setUsername(userName);
    user.setPassword(rawPassword);
    user.setEmail(userEmail);
    user.setProfile(profileImg);

    UserStatus userStatus = new UserStatus();
    userStatus.setUser(user);

    // 신규 유저 생성시 공용 채널의 readStatus 자동으로 추가
    List<Channel> channels = channelRepository.findAllPublicChannel();
    for (Channel channel : channels) {
      ReadStatus readStatus = new ReadStatus();
      readStatus.setChannel(channel);
      readStatus.setUser(user);
      readStatusRepository.createReadStatus(readStatus);
    }

    userRepository.createUser(user);
    return getUserDtoByUser(user);
  }


  @Override
  public UserDto updateUser(UserUpdateRequest userUpdateRequest, MultipartFile profileImage)
      throws IOException {
    User targetUser = findUserByUserId(userUpdateRequest.getUserId());

    // 사용자 기본 정보 수정
    if (userUpdateRequest.getNewEmail() != null && !userUpdateRequest.getNewEmail()
        .equals(targetUser.getEmail())) {
      targetUser.setEmail(userUpdateRequest.getNewEmail());
    }

    if (userUpdateRequest.getNewUsername() != null && !userUpdateRequest.getNewUsername()
        .equals(targetUser.getUsername())) {
      targetUser.setUsername(userUpdateRequest.getNewUsername());
    }

    if (userUpdateRequest.getNewPassword() != null && !userUpdateRequest.getNewPassword()
        .equals(targetUser.getPassword())) {
      targetUser.setPassword(userUpdateRequest.getNewPassword());
    }

    if (profileImage != null && !profileImage.isEmpty()) {
      BinaryContent newProfileImg = new BinaryContent();
      newProfileImg.setSize(profileImage.getSize());
      newProfileImg.setContentType(profileImage.getContentType());
      newProfileImg.setBytes(profileImage.getBytes());
      newProfileImg.setFileName(profileImage.getName());
      targetUser.setProfile(newProfileImg);
    }

    // DB에 사용자 정보 반영
    userRepository.updateUser(targetUser);
    return getUserDtoByUser(targetUser);
  }

  @Override
  public void deleteUser(UUID userId) {
    User user = findUserByUserId(userId);
    userRepository.deleteUser(user);
  }

  @Override
  public List<UserDto> findAllUserDto() {
    List<User> users = userRepository.loadUsers();
    List<UserDto> userList = new ArrayList<>();

    for (User user : users) {
      UserDto userDto = getUserDtoByUser(user);
      userList.add(userDto);
    }

    return userList;
  }

  @Override
  public User findUserByUserId(UUID userId) {
    Optional<User> user = userRepository.findUserByUserId(userId);
    if (user.isEmpty()) {
      throw new NoFindUserException("유저를 찾을 수 없습니다.", "Id: " + userId + " 는 없는 유저입니다.");
    }
    return user.get();
  }

  @Override
  public UserDto findUserDtoByUserId(UUID userId) {return getUserDtoByUserId(userId);}

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

  private UserDto getUserDtoByUserId(UUID userId) {
    User user = findUserByUserId(userId);
    return getUserDtoByUser(user);
  }

  private UserDto getUserDtoByUser(User user) {
    UserStatus userStatus = user.getStatus();
    Duration duration = Duration.between(userStatus.getLastActiveAt(), Instant.now());
    Boolean loginStatus = duration.toMinutes() < 5;

    BinaryContent binaryContent = user.getProfile();
    BinaryContentDto binaryContentDto = null;

    if (binaryContent != null) {
      binaryContentDto = new BinaryContentDto(
          binaryContent.getId(),
          binaryContent.getFileName(),
          binaryContent.getSize(),
          binaryContent.getContentType()
      );
    }

    return new UserDto(user.getId(), user.getUsername(), user.getEmail(), binaryContentDto, loginStatus);
  }
}
