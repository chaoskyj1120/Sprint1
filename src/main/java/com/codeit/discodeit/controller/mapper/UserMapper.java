package com.codeit.discodeit.controller.mapper;

import com.codeit.discodeit.dto.binary_contents_dto.BinaryContentDto;
import com.codeit.discodeit.dto.user_service_dto.UserDto;
import com.codeit.discodeit.entity.BinaryContent;
import com.codeit.discodeit.entity.User;
import com.codeit.discodeit.entity.UserStatus;

import java.time.Duration;
import java.time.Instant;

public class UserMapper {

  public static UserDto toUserDto(User user) {
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
