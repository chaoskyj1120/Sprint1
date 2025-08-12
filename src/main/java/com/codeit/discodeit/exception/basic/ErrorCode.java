package com.codeit.discodeit.exception.basic;

import lombok.Getter;

public interface ErrorCode {
  int getStatus();
  String getMessage();
  String getName();
}