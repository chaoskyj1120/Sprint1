package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binary_contents_dto.BinaryContentsResponseDto;
import com.sprint.mission.discodeit.dto.binary_contents_dto.CreateBinaryContentsRequestDto;
import com.sprint.mission.discodeit.dto.binary_contents_dto.FindBinaryContentRequestDto;

import java.util.List;
import java.util.UUID;

public interface BinaryContentsService {
    BinaryContentsResponseDto createBinaryContents(CreateBinaryContentsRequestDto createBinaryContentsRequestDto);
    List<BinaryContentsResponseDto> findAllBinaryContentsDTOs();
    BinaryContentsResponseDto findBinaryContentsDTOByBinaryContentsId(UUID binaryContentId);
    void deleteBinaryContentsDTOById(UUID binaryContentId);

    List<BinaryContentsResponseDto> findBinaryContentsDtosByReferenceId(UUID referenceId);
}
