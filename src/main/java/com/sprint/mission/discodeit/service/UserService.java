package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user_service_dto.*;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;

public interface UserService {

    void updateUser(UserUpdateRequestDTO userUpdateRequestDTO);
    void deleteUser(UserDTO userDTO);
    void restoreUser(String userName);
    UserDTO createUser(UserCreateRequestDTO userCreateRequestDTO);

    List<UserDTO> findAllUserDTO();
    List<UserDTO> findAllActiveUserDTO();
    List<UserDTO> findAllDeactiveUserDTO();

    List<UserRecentConnectionDTO> findAllConnection();
    UserRecentConnectionDTO findUserConnectionByUserName(UserConnectionRequestDTO userConnectionRequestDTO);

    void logOutUser(UserDTO userDTO);
    UserStatus isOnline(User user);

}
