package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user_service_dto.*;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;

public interface UserService {

    void updateUser(UserUpdateRequestDto userUpdateRequestDTO);
    void deleteUser(UserResponseDto userResponseDto);
    void restoreUser(String userName);
    UserResponseDto createUser(UserCreateRequestDto userCreateRequestDTO);

    List<UserResponseDto> findAllUserDTO();
    List<UserResponseDto> findAllActiveUserDTO();
    List<UserResponseDto> findAllDeactiveUserDTO();

    List<UserRecentConnectionDto> findAllConnection();
    UserRecentConnectionDto findUserConnectionByUserName(UserConnectionRequestDto userConnectionRequestDTO);

    void logOutUser(UserResponseDto userResponseDto);
    UserStatus isOnline(User user);

}
