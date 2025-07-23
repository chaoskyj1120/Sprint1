package com.codeit.discodeit.service.basic;

import com.codeit.discodeit.dto.auth_service_dto.LoginRequestDto;
import com.codeit.discodeit.entity.BinaryContent;
import com.codeit.discodeit.entity.User;
import com.codeit.discodeit.exception.exception.NoFindUserException;
import com.codeit.discodeit.exception.exception.WrongPasswordException;
import com.codeit.discodeit.repository.BinaryContentRepository;
import com.codeit.discodeit.repository.UserRepository;
import com.codeit.discodeit.service.AuthService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;

  @Override
  public User logInUser(LoginRequestDto loginRequest) {

    User user = findUserByUserName(loginRequest.getUsername());
    String rawPassword = loginRequest.getPassword();

    if (!rawPassword.equals(user.getPassword())) {
      throw new WrongPasswordException("비밀번호가 일치하지 않음", "Wrong password");
    }
    return user;
  }

  private User findUserByUserName(String userName) {
    return userRepository.findUserByUserName(userName)
        .orElseThrow(() -> new NoFindUserException("사용자를 찾을 수 없음",
            ("User with username {" + userName + "} not found")));
  }
}
