package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContents;

import java.util.List;
import java.util.UUID;

public interface BinaryContentsRepository {

    List<BinaryContents> loadBinaryContents();
    void saveBinaryContents(List<BinaryContents> contentsList);

    void createBinaryContents(BinaryContents contents);
    BinaryContents getBinaryContentsByBinaryContentsId(UUID binaryContentsId);

    void delete(UUID profileId);
   List<BinaryContents> findBinaryContentsByReferenceId(UUID referenceId);
}
