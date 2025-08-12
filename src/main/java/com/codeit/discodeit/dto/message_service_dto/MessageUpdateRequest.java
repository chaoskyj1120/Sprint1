package com.codeit.discodeit.dto.message_service_dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MessageUpdateRequest {
  @NotEmpty
  String newContent;
}
