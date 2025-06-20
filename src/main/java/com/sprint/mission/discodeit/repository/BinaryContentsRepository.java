package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContents;

import java.util.List;

public interface BinaryContentsRepository {

    List<BinaryContents> loadBinaryContents();
    void saveBinaryContents(List<BinaryContents> contentsList);

    void createBinaryContents(BinaryContents contents);
}
