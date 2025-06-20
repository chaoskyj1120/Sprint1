package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.UUID;

public interface UserService {

    void updateUser(User user, String newUserName);
    void deleteUser(User user);
    void restoreUser(User user);
    User createUser(String idForLogin, String passwordForLogin, String userName, String email, String profilePicturePath);

    void printUser(User user);
    void printAllUsers();
    void printActiveUsers();
    void printDeactivatedUsers();
}
