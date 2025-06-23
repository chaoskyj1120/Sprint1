package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;

public interface ReadStatusRepository {

    List<ReadStatus> loadReadStatuses();
    void saveReadStatuses(List<ReadStatus> readStatuses);
    void createReadStatus(ReadStatus readStatus);
}
