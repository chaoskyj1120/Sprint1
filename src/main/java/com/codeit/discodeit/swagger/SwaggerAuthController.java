package com.codeit.discodeit.swagger;

import com.codeit.discodeit.dto.auth_service_dto.LoginRequestDto;
import com.codeit.discodeit.dto.user_service_dto.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface SwaggerAuthController {
  ResponseEntity<UserDto> loginUser(@RequestBody LoginRequestDto loginRequestDto);
}
