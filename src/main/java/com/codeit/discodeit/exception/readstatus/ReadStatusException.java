package com.codeit.discodeit.exception.readstatus;

import com.codeit.discodeit.exception.basic.DiscodeitException;
import com.codeit.discodeit.exception.basic.ErrorCode;
import java.time.Instant;
import java.util.Map;
import lombok.Getter;

@Getter
public class ReadStatusException extends DiscodeitException {
  public ReadStatusException(Instant timestamp, ErrorCode errorCode,
      Map<String, Object> details) {
    super(timestamp, errorCode, details);
  }
}
