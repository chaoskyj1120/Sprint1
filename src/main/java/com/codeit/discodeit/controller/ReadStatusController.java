package com.codeit.discodeit.controller;

import com.codeit.discodeit.mapper.ReadStatusMapper;
import com.codeit.discodeit.dto.readstatus_dto.ReadStatusCreateRequest;
import com.codeit.discodeit.dto.readstatus_dto.ReadStatusDto;
import com.codeit.discodeit.dto.readstatus_dto.ReadStatusUpdateRequest;
import com.codeit.discodeit.entity.ReadStatus;
import com.codeit.discodeit.entity.User;
import com.codeit.discodeit.service.ReadStatusService;
import com.codeit.discodeit.service.UserService;
import com.codeit.discodeit.swagger.SwaggerReadStatusController;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@Tag(name = "ReadStatus", description = "Message 읽음 상태 API")
@RequestMapping("/api/readStatuses")
public class ReadStatusController implements SwaggerReadStatusController {

  private final UserService userService;
  private final ReadStatusService readStatusService;
  private final ReadStatusMapper readStatusMapper;

  @PostMapping
  public ResponseEntity<ReadStatusDto> createReadStatus(
      @RequestBody ReadStatusCreateRequest readStatusCreateRequest) {

    ReadStatus readStatus = readStatusService
        .findReadStatusByUserIdAndChannelId(readStatusCreateRequest.getUserId(), readStatusCreateRequest.getChannelId());

    ReadStatusDto readStatusDto = readStatusMapper.toReadStatusDto(readStatus);
    return ResponseEntity
        .status(HttpStatus.CREATED) // 201 Created
        .body(readStatusDto);
  }

  @GetMapping
  public ResponseEntity<List<ReadStatusDto>> findAllByUserId(@RequestParam UUID userId) {
    User user = userService.findUserDtoByUserId(userId);
    List<ReadStatus> readStatusesList =
        readStatusService.findReadStatusesByUserId(user.getId());
    List<ReadStatusDto> readStatusDtoList = readStatusesList.stream()
        .map(readStatusMapper::toReadStatusDto)
        .toList();

    return ResponseEntity.ok(readStatusDtoList);
  }

  @PatchMapping("/{readStatusId}")
  public ResponseEntity<ReadStatusDto> update(
      @Parameter(
          name = "readStatusId",
          description = "수정할 읽음 상태 ID",
          required = true
      )
      @PathVariable("readStatusId") UUID readStatusId,
      @RequestBody ReadStatusUpdateRequest readStatusUpdateRequest) {
    ReadStatus updateReadStatus = readStatusService.updateReadStatusByReadStatusId(
        readStatusId, readStatusUpdateRequest);

    ReadStatusDto readStatusDto = readStatusMapper.toReadStatusDto(updateReadStatus);
    return ResponseEntity.ok(readStatusDto);
  }
}