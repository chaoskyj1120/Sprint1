package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel_service_dto.ChannelDTO;
import com.sprint.mission.discodeit.dto.readstatus_dto.CreateReadStatusRequestDTO;
import com.sprint.mission.discodeit.dto.readstatus_dto.DeleteReadStatusRequestDTO;
import com.sprint.mission.discodeit.dto.readstatus_dto.ReadStatusDTO;
import com.sprint.mission.discodeit.dto.readstatus_dto.UpdateReadStatusRequestDTO;
import com.sprint.mission.discodeit.dto.user_service_dto.UserDTO;

import java.util.List;

public interface ReadStatusService {
    ReadStatusDTO createReadStatus(CreateReadStatusRequestDTO createReadStatusRequestDTO);

    List<ReadStatusDTO> findReadStatusByUserDTO(UserDTO userDTO);

    List<ReadStatusDTO> findReadStatusByChannelDTO(ChannelDTO channelDTO);

    ReadStatusDTO findReadStatusByReadStatusDTO(ReadStatusDTO readStatusDTO);

    void updateReadStatus(UpdateReadStatusRequestDTO updateReadStatusRequestDTO);

    void deleteReadStatus(DeleteReadStatusRequestDTO deleteReadStatusRequestDTO);
}
