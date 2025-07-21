package com.codeit.discodeit.dto.binary_contents_dto;

import java.util.UUID;

public record BinaryContentDto(
    UUID id,
    String fileName,
    Integer size,
    String contentType
) {}
