package com.codeit.discodeit.controller;

import com.codeit.discodeit.dto.user_service_dto.UserDto;
import com.codeit.discodeit.dto.readstatus_dto.ReadStatusCreateRequest;
import com.codeit.discodeit.dto.readstatus_dto.ReadStatusDto;
import com.codeit.discodeit.dto.readstatus_dto.ReadStatusUpdateRequest;
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

  @PostMapping
  public ResponseEntity<ReadStatusDto> createReadStatus(
      @RequestBody ReadStatusCreateRequest readStatusCreateRequest) {

    ReadStatusDto readStatusDto = readStatusService
        .findReadStatusByUserIdAndChannelId(readStatusCreateRequest.getUserId(), readStatusCreateRequest.getChannelId());

    return ResponseEntity
        .status(HttpStatus.CREATED) // 201 Created
        .body(readStatusDto);
  }

  @GetMapping
  public ResponseEntity<List<ReadStatusDto>> findAllByUserId(@RequestParam UUID userId) {
    UserDto user = userService.findUserDtoByUserId(userId);
    List<ReadStatusDto> readStatusDtoList =
        readStatusService.findReadStatuseDtoListByUserId(user.id());

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
    ReadStatusDto updateReadStatusDto = readStatusService.updateReadStatusByReadStatusId(
        readStatusId, readStatusUpdateRequest);

    return ResponseEntity.ok(updateReadStatusDto);
  }
}