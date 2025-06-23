package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.LoginRequestDTO;
import com.sprint.mission.discodeit.dto.UserLoggedDataDTO;
import com.sprint.mission.discodeit.dto.UserLoginDataDTO;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;

public interface AuthService {
    UserLoginDataDTO logInUser(LoginRequestDTO loginRequestDTO);
}
