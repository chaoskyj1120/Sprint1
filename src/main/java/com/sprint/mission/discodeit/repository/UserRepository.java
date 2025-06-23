package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserRepository {

    void createUser(User user);
    void updateUser(List<User> users);
    void deleteUser(User user);
    void restoreUser(User user);

    List<User> loadUsers();
    void saveUsers(List<User> users);

    User getUserById(UUID id);
}