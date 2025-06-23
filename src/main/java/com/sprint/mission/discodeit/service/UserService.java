package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserCreateDTO;
import com.sprint.mission.discodeit.dto.UserLoggedDataDTO;
import com.sprint.mission.discodeit.dto.UserUpdateDTO;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;

public interface UserService {

    void updateUser(UserUpdateDTO userUpdateDTO);
    void deleteUser(User user);
    void restoreUser(User user);
    User createUser(UserCreateDTO userCreateDto);

    void printUser(User user);
    void printAllUsers();
    void printActiveUsers();
    void printDeactivatedUsers();

    List<UserLoggedDataDTO> findAll();
    UserLoggedDataDTO findUserById(String userIdForLogin);
}
