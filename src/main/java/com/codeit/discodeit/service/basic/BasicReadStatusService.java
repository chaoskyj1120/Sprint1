package com.codeit.discodeit.service.basic;

import com.codeit.discodeit.dto.readstatus_dto.ReadStatusUpdateRequest;
import com.codeit.discodeit.entity.Channel;
import com.codeit.discodeit.entity.ReadStatus;
import com.codeit.discodeit.entity.User;
import com.codeit.discodeit.exception.exception.NoFindReadStatusException;
import com.codeit.discodeit.repository.ReadStatusRepository;
import com.codeit.discodeit.service.ReadStatusService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

  private final ReadStatusRepository readStatusRepository;

  @Override
  public void createReadStatus(User user, Channel channel){
    ReadStatus readStatus = new ReadStatus();
    readStatus.setChannel(channel);
    readStatus.setUser(user);
    readStatusRepository.createReadStatus(readStatus);
  }

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

  @Override
  public ReadStatus findReadStatusByUserIdAndChannelId(UUID userId, UUID channelId) {
    Optional<ReadStatus> readStatus = readStatusRepository.findReadStatusesByUserIdAndChannelId(userId, channelId);
    if (readStatus.isEmpty()) {
      throw new NoFindReadStatusException("해당하는 ReadStatus를 찾을 수 없습니다.",
          "userId:  {" + userId + "}, channelId: {"+channelId+"} isn't exists");
    }

    return readStatus.get();
  }

  @Override
  public List<ReadStatus> findReadStatusesByChannelId(Channel channel){
    return readStatusRepository.findReadStatusByChannel(channel);
  }

  private ReadStatus findReadStatusByReadStatusId(UUID readStatusId) {
    return readStatusRepository.findReadStatusesByReadStatusId(readStatusId)
        .orElseThrow(() -> new NoFindReadStatusException("해당하는 ReadStatus를 찾을 수 없습니다.",
            "ReadStatusId  {" + readStatusId + "} isn't exists"));
  }
}