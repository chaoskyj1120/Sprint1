package com.codeit.discodeit.controller.mapper;

import com.codeit.discodeit.dto.channel_service_dto.ChannelDto;
import com.codeit.discodeit.dto.user_service_dto.UserDto;
import com.codeit.discodeit.entity.Channel;
import com.codeit.discodeit.entity.Message;
import com.codeit.discodeit.entity.ReadStatus;
import java.util.List;
import java.util.Optional;
public class ChannelMapper {

  public static ChannelDto toDto(Channel channel, Optional<Message> lastMessage, List<ReadStatus> readStatuses) {
    ChannelDto dto = new ChannelDto();
    dto.setId(channel.getId());
    dto.setName(channel.getName());
    dto.setDescription(channel.getDescription());
    dto.setType(channel.getType());

    dto.setLastMessageAt(lastMessage.map(Message::getCreatedAt).orElse(null));

    List<UserDto> participants = readStatuses.stream()
        .map(rs -> UserMapper.toUserDto(rs.getUser()))
        .toList();

    dto.setParticipants(participants);
    return dto;
  }
}
