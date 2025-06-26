package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binary_contents_dto.BinaryContentsResponseDto;
import com.sprint.mission.discodeit.dto.binary_contents_dto.FindBinaryContentRequestDto;

import java.util.List;

public interface BinaryContentsService {
    List<BinaryContentsResponseDto> findAllBinaryContentsDTOs();
    List<BinaryContentsResponseDto> findBinaryContentsDTOsByReferenceId(FindBinaryContentRequestDto findBinaryContentRequestDTO);
}
