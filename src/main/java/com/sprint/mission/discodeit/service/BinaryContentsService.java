package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binary_contents_dto.BinaryContentsDTO;
import com.sprint.mission.discodeit.dto.binary_contents_dto.FindBinaryContentRequestDTO;

import java.util.List;

public interface BinaryContentsService {
    List<BinaryContentsDTO> findAllBinaryContentsDTOs();
    List<BinaryContentsDTO> findBinaryContentsDTOsByReferenceId(FindBinaryContentRequestDTO findBinaryContentRequestDTO);
}
