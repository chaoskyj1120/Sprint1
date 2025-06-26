package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binary_contents_dto.BinaryContentsResponseDto;
import com.sprint.mission.discodeit.dto.binary_contents_dto.FindBinaryContentRequestDto;
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
    public List<BinaryContentsResponseDto> findAllBinaryContentsDTOs(){
        List<BinaryContents> binaryContents = binaryContentsRepository.loadBinaryContents();
        List<BinaryContentsResponseDto> binaryContentsResponseDtos = new ArrayList<>();

        for (BinaryContents binaryContent : binaryContents){
            binaryContentsResponseDtos.add(new BinaryContentsResponseDto(binaryContent));
        }
        return binaryContentsResponseDtos;
    }

    @Override
    public List<BinaryContentsResponseDto> findBinaryContentsDTOsByReferenceId(FindBinaryContentRequestDto findBinaryContentRequestDTO){
        List<BinaryContentsResponseDto> binaryContentsResponseDtos = new ArrayList<>();
        List<BinaryContents> binaryContents = binaryContentsRepository.findBinaryContentsByReferenceId(findBinaryContentRequestDTO.getReferenceId());

        for (BinaryContents binaryContent : binaryContents){
            binaryContentsResponseDtos.add(new BinaryContentsResponseDto(binaryContent));
        }
        return binaryContentsResponseDtos;
    }

}
