package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.ArrayList;

public interface UserRepository {
    void updateUser(User user, String newUserName);
    void deleteUser(User user);
    void restoreUser(User user);
    User createUser(String userName);
}
