package com.codeit.discodeit.controller.mapper;

import com.codeit.discodeit.dto.user_status_dto.UserStatusDto;
import com.codeit.discodeit.entity.User;
import com.codeit.discodeit.entity.UserStatus;
import org.mapstruct.control.MappingControl.Use;

public class UserStatusMapper {

  public static UserStatusDto userStatusDto(UserStatus userStatus){
    return new UserStatusDto(userStatus.getId(), userStatus.getUser().getId(), userStatus.getLastActiveAt());

  }
}
