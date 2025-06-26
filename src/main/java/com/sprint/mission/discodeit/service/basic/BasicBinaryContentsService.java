package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binary_contents_dto.BinaryContentsDTO;
import com.sprint.mission.discodeit.dto.binary_contents_dto.FindBinaryContentRequestDTO;
import com.sprint.mission.discodeit.entity.BinaryContents;
import com.sprint.mission.discodeit.repository.BinaryContentsRepository;
import com.sprint.mission.discodeit.service.BinaryContentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class BasicBinaryContentsService implements BinaryContentsService {

    private final BinaryContentsRepository binaryContentsRepository;


    @Override
    public List<BinaryContentsDTO> findAllBinaryContentsDTOs(){
        List<BinaryContents> binaryContents = binaryContentsRepository.loadBinaryContents();
        List<BinaryContentsDTO> binaryContentsDTOs = new ArrayList<>();

        for (BinaryContents binaryContent : binaryContents){
            binaryContentsDTOs.add(new BinaryContentsDTO(binaryContent));
        }
        return binaryContentsDTOs;
    }

    @Override
    public List<BinaryContentsDTO> findBinaryContentsDTOsByReferenceId(FindBinaryContentRequestDTO findBinaryContentRequestDTO){
        List<BinaryContentsDTO> binaryContentsDTOs = new ArrayList<>();
        List<BinaryContents> binaryContents = binaryContentsRepository.findBinaryContentsByReferenceId(findBinaryContentRequestDTO.getReferenceId());

        for (BinaryContents binaryContent : binaryContents){
            binaryContentsDTOs.add(new BinaryContentsDTO(binaryContent));
        }
        return binaryContentsDTOs;
    }

}
