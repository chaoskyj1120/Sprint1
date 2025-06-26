package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.auth_service_dto.LoginRequestDTO;
import com.sprint.mission.discodeit.dto.user_service_dto.UserDTO;

public interface AuthService {
    UserDTO logInUser(LoginRequestDTO loginRequestDTO);
}
