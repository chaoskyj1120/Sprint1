package com.codeit.discodeit.service.basic;

import com.codeit.discodeit.dto.readstatus_dto.ReadStatusUpdateRequest;
import com.codeit.discodeit.entity.ReadStatus;
import com.codeit.discodeit.exception.exception.NoFindReadStatusException;
import com.codeit.discodeit.repository.ChannelRepository;
import com.codeit.discodeit.repository.ReadStatusRepository;
import com.codeit.discodeit.repository.UserRepository;
import com.codeit.discodeit.service.ReadStatusService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;

  @Override
  public List<ReadStatus> findReadStatusesByUserId(UUID userId) {
    return readStatusRepository.findReadStatusesByUserId(userId);
  }

  @Override
  public ReadStatus updateReadStatusByReadStatusId(UUID readStatusId,
      ReadStatusUpdateRequest readStatusUpdateRequest) {
    ReadStatus readStatus = findReadStatusByReadStatusId(readStatusId);

    if (readStatus == null) {
      throw new NoFindReadStatusException("Message 읽음 상태를 찾을 수 없음", readStatusId + " is not found");
    }

    readStatus.setLastReadAt(readStatusUpdateRequest.getNewLastReadAt());
    readStatusRepository.updateReadStatus(readStatus);

    return readStatus;
  }

  private ReadStatus findReadStatusByReadStatusId(UUID readStatusId) {
    return readStatusRepository.findReadStatusesByReadStatusId(readStatusId)
        .orElseThrow(() -> new NoFindReadStatusException("해당하는 ReadStatus를 찾을 수 없습니다.",
            "ReadStatusId  {" + readStatusId + "} isn't exists"));
  }
}