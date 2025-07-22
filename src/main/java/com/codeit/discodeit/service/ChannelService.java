package com.codeit.discodeit.service;

import com.codeit.discodeit.dto.channel_service_dto.*;
import com.codeit.discodeit.entity.Channel;
import java.util.List;
import java.util.UUID;

public interface ChannelService {

  ChannelDto createPublicChannel(
      CreatePublicChannelRequestDto createPublicChannelRequestDTO);

  ChannelDto createPrivateChannel(
      PrivateChannelCreateRequest privateChannelCreateRequest);

  void deleteChannel(UUID channelId);

  ChannelDto updatePublicChannel(UUID channelId,
      PublicChannelUpdateRequest publicChannelUpdateRequest);
}
