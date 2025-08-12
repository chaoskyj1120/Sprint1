package com.codeit.discodeit.exception.user;

import com.codeit.discodeit.exception.basic.ErrorCode;
import java.time.Instant;
import java.util.Map;

public class UserNotFoundException extends UserException {
  public UserNotFoundException(Map<String, Object> details) {
    super(Instant.now(), UserErrorCode.USER_NOT_FOUND, details);
  }
}
