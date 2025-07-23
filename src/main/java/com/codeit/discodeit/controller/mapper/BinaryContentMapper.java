package com.codeit.discodeit.controller.mapper;

import com.codeit.discodeit.dto.binary_contents_dto.BinaryContentDto;
import com.codeit.discodeit.entity.BinaryContent;

public class BinaryContentMapper {

  public static BinaryContentDto toBinaryContentDto(
      BinaryContent binaryContent) {
    return new BinaryContentDto(binaryContent.getId(),
        binaryContent.getFileName(), binaryContent.getSize(), binaryContent.getContentType());
  }
}
