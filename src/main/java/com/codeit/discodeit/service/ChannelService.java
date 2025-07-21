package com.codeit.discodeit.service;

import com.codeit.discodeit.dto.channel_service_dto.*;
import com.codeit.discodeit.entity.Channel;
import java.util.List;
import java.util.UUID;

public interface ChannelService {

  ChannelDto createPublicChannel(
      CreatePublicChannelRequestDto createPublicChannelRequestDTO);

  ChannelDto createPrivateChannel(
      CreatePrivateChannelRequestDto createPrivateChannelRequestDto);

  void deleteChannel(UUID channelId);


  List<ChannelDto> findChannelDtoListByUserId(UUID userId);

  ChannelDto updatePublicChannel(UUID channelId,
      PublicChannelUpdateRequest publicChannelUpdateRequest);

  Channel findChannelByChannelId(UUID channelId);

  ChannelDto findChannelDtoByChannelId(UUID channelId);
}
