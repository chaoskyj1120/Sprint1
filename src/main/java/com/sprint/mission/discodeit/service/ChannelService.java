package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel_service_dto.*;
import com.sprint.mission.discodeit.dto.user_service_dto.UserDTO;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    ChannelDTO createPublicChannel(CreateChannelRequestDTO createChannelRequestDTO);
    ChannelDTO createPrivateChannel(CreateChannelRequestDTO createChannelRequestDTO, UserDTO enterUserDTO);

    void deleteChannel(DeleteChannelRequestDTO deleteChannelRequestDTO);

    void addUserToChannel(AddUserToChannelRequestDTO addUserToChannelRequestDTO);
    void leaveUserFromChannel(LeaveUserFromChannelRequestDTO leaveUserFromChannelRequestDTO);

    void updateChannelName(ChannelNameUpdateRequestDTO channelNameUpdateRequestDTO);
    void updateHostUser(ChannelHostUserUpdateRequestDTO channelHostUserUpdateRequestDTO);

    List<ChannelDTO> findPublicChannel();
    List<ChannelDTO> findPrivateChannel(UserDTO user);

    ChannelDTO enterChanner(User user, Channel chanel);
    ChannelDTO findChannelDTOByCannelId(UUID chanelId);

    List<ReadStatus> findAllReadStatus();
}
