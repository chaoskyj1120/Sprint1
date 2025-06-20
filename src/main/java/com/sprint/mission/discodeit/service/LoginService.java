package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

public interface LoginService {
    boolean authenticateUser(String idForLogin, String passwordForLogin);
    User logInUser(String idForLogin);
    void logOutUser(User user);
    boolean isOnline(User user);

}
