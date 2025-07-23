package com.codeit.discodeit.service;

import com.codeit.discodeit.dto.channel_service_dto.*;
import com.codeit.discodeit.entity.Channel;
import java.util.List;
import java.util.UUID;

public interface ChannelService {

  Channel createPublicChannel(
      CreatePublicChannelRequestDto createPublicChannelRequestDTO);

  Channel createPrivateChannel(
      PrivateChannelCreateRequest privateChannelCreateRequest);

  void deleteChannel(UUID channelId);

  Channel updatePublicChannel(UUID channelId,
      PublicChannelUpdateRequest publicChannelUpdateRequest);
}
