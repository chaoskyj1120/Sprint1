package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.UUID;

public interface ReadStatusRepository {

    List<ReadStatus> loadReadStatuses();
    void saveReadStatuses(List<ReadStatus> readStatuses);
    void createReadStatus(ReadStatus readStatus);

    List<ReadStatus> findReadStatusesByUserId(UUID userId);
    List<ReadStatus> findReadStatusesByChannelId(UUID channelId);
    void deleteReadStatus(UUID userId, UUID channelId);
    void deleteReadStatusByChannelId(UUID channelId);
    ReadStatus findReadStatusesByUserIdAndChannelId(UUID userId, UUID channelId);
    void updateReadStatus(ReadStatus readStatus);
    ReadStatus findReadStatusesByreadStatusId(UUID readStatusId);

    void deleteReadStatusByReadStatusId(UUID readStatusId);
}
