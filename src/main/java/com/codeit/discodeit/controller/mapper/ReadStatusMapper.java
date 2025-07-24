package com.codeit.discodeit.controller.mapper;

import com.codeit.discodeit.dto.readstatus_dto.ReadStatusDto;
import com.codeit.discodeit.entity.ReadStatus;

public class ReadStatusMapper {

  public static ReadStatusDto toReadStatusDto(ReadStatus readStatus) {
    return new ReadStatusDto(readStatus.getId(), readStatus.getUser().getId(), readStatus.getChannel().getId(), readStatus.getLastReadAt());
  }
}
