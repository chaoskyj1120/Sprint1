package com.codeit.discodeit.controller.mapper;

import com.codeit.discodeit.dto.binary_contents_dto.BinaryContentDto;
import com.codeit.discodeit.dto.message_service_dto.MessageDto;
import com.codeit.discodeit.dto.user_service_dto.UserDto;
import com.codeit.discodeit.entity.BinaryContent;
import com.codeit.discodeit.entity.Message;
import java.util.List;
import java.util.stream.Collectors;

public class MessageMapper {

  public static MessageDto toMessageDto(Message message) {
    UserDto userDto = UserMapper.toUserDto(message.getAuthor());
    List<BinaryContent> binaryContentList = message.getAttachments();
    List<BinaryContentDto> binaryContentDtoList = binaryContentList.stream()
        .map(BinaryContentMapper::toBinaryContentDto)
        .toList();

    return new MessageDto(
        message.getId(),
        message.getCreatedAt(),
        message.getUpdatedAt(),
        message.getContent(),
        message.getChannel().getId(),
        userDto,
        binaryContentDtoList);
  }
}
