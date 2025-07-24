package com.codeit.discodeit.dto.readstatus_dto;

import com.codeit.discodeit.dto.binary_contents_dto.BinaryContentDto;
import com.codeit.discodeit.dto.user_service_dto.UserDto;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ReadStatusDto(
    UUID id,
    UUID userId,
    UUID channelId,
    Instant lastReadAt
) {
}