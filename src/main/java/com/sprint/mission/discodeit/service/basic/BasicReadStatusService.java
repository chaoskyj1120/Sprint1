package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel_service_dto.ChannelDTO;
import com.sprint.mission.discodeit.dto.readstatus_dto.CreateReadStatusRequestDTO;
import com.sprint.mission.discodeit.dto.readstatus_dto.DeleteReadStatusRequestDTO;
import com.sprint.mission.discodeit.dto.readstatus_dto.ReadStatusDTO;
import com.sprint.mission.discodeit.dto.readstatus_dto.UpdateReadStatusRequestDTO;
import com.sprint.mission.discodeit.dto.user_service_dto.UserDTO;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public ReadStatusDTO createReadStatus (CreateReadStatusRequestDTO createReadStatusRequestDTO){

        // 원래는 유저에서 했던 기능인데 가져옴
        UUID userId = createReadStatusRequestDTO.getUserDTO().getUserId();
        UUID channelId = createReadStatusRequestDTO.getChannelDTO().getChannelId();

        Optional<User> user = userRepository.findUserByUserId(userId);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("해당 유저가 존재하지 않습니다: " + userId);
        }

        // 2. 채널이 존재하지 않으면 예외 발생
        Optional<Channel> channel = channelRepository.findChannelByChannelId(channelId);
        if (channel.isEmpty()) {
            throw new IllegalArgumentException("해당 채널이 존재하지 않습니다: " + channelId);
        }

        ReadStatus existing = readStatusRepository.findReadStatusesByUserIdAndChannelId(userId, channelId);
        if (existing != null) {
            throw new IllegalStateException("이미 존재하는 ReadStatus입니다: userId = " + userId + ", channelId = " + channelId);
        }

        ReadStatus newReadStatus = new ReadStatus(channelId, userId); // 생성자에 messageId 반영
        readStatusRepository.createReadStatus(newReadStatus);

        return new ReadStatusDTO(newReadStatus);
    }

    @Override
    public List<ReadStatusDTO> findReadStatusByUserDTO (UserDTO userDTO){
        List<ReadStatus> readStatuses = readStatusRepository.findReadStatusesByUserId(userDTO.getUserId());
        List<ReadStatusDTO> readStatusDTOs = new ArrayList<>();

        for (ReadStatus readStatus : readStatuses) {
            readStatusDTOs.add(new ReadStatusDTO(readStatus));
        }

        return readStatusDTOs;
    }

    @Override
    public List<ReadStatusDTO> findReadStatusByChannelDTO (ChannelDTO channelDTO){
        List<ReadStatus> readStatuses = readStatusRepository.findReadStatusesByUserId(channelDTO.getChannelId());
        List<ReadStatusDTO> readStatusDTOs = new ArrayList<>();

        for (ReadStatus readStatus : readStatuses) {
            readStatusDTOs.add(new ReadStatusDTO(readStatus));
        }
        return readStatusDTOs;
    }

    @Override
    public ReadStatusDTO findReadStatusByReadStatusDTO (ReadStatusDTO readStatusDTO){
        ReadStatus readStatus = readStatusRepository.findReadStatusesByreadStatusId(readStatusDTO.getReadStatusId());
        return new ReadStatusDTO(readStatus);
    }

    @Override
    public void updateReadStatus (UpdateReadStatusRequestDTO updateReadStatusRequestDTO){

        ReadStatus readStatus = readStatusRepository.findReadStatusesByreadStatusId(updateReadStatusRequestDTO.getReadStatusId());

        if (readStatus == null) {
            throw new IllegalStateException("존재하지 않는 readStatus입니다.");
        }

        if (readStatus.getUserId() != updateReadStatusRequestDTO.getUserId()) {
            throw new IllegalArgumentException("유저 id와 readStatus id가 불일치 합니다.");
        }
        
        if (readStatus.getChannelId() != updateReadStatusRequestDTO.getChannelId()) {
            throw new IllegalArgumentException("채널 id와 readStatus id가 불일치 합니다.");
        }

        readStatus.updateUpdatedAt();// 시간만 업데이트
        readStatusRepository.updateReadStatus(readStatus);
    }

    @Override
    public void deleteReadStatus (DeleteReadStatusRequestDTO deleteReadStatusRequestDTO){
        ReadStatus readStatus = readStatusRepository.findReadStatusesByreadStatusId(deleteReadStatusRequestDTO.getReadStatusId());
        if (readStatus == null) {
            throw new IllegalStateException("존재하지 않는 readStatus입니다.");
        }

        if (readStatus.getUserId() != deleteReadStatusRequestDTO.getUserId()) {
            throw new IllegalArgumentException("유저 id와 readStatus id가 불일치 합니다.");
        }

        if (readStatus.getChannelId() != deleteReadStatusRequestDTO.getChannelId()) {
            throw new IllegalArgumentException("채널 id와 readStatus id가 불일치 합니다.");
        }
        readStatusRepository.deleteReadStatusByReadStatusId(deleteReadStatusRequestDTO.getReadStatusId());
    }
}
