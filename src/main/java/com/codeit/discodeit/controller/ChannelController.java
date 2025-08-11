package com.codeit.discodeit.controller;

import com.codeit.discodeit.dto.channel_service_dto.*;
import com.codeit.discodeit.service.ChannelService;
import com.codeit.discodeit.service.MessageService;
import com.codeit.discodeit.service.ReadStatusService;
import com.codeit.discodeit.swagger.SwaggerChannelController;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Channel", description = "Channel API")
@RequestMapping("/api/channels")
public class ChannelController implements SwaggerChannelController {

  private final ChannelService channelService;
  private final MessageService messageService;
  private final ReadStatusService readStatusService;

  @PostMapping(value = "/public", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ChannelDto> createPublicChannel(
      @RequestBody CreatePublicChannelRequestDto createPublicChannelRequestDto) {
    ChannelDto channelDto = channelService.createPublicChannel(createPublicChannelRequestDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(channelDto);
  }


  @PostMapping(value = "/private", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ChannelDto> createPrivateChannel(
      @RequestBody PrivateChannelCreateRequest privateChannelCreateRequest) {
    ChannelDto channelDto = channelService.createPrivateChannel(privateChannelCreateRequest.getParticipantIds());
    return ResponseEntity.status(HttpStatus.CREATED).body(channelDto);
  }

  @DeleteMapping("/{channelId}")
  public ResponseEntity<Void> deleteChannel(@PathVariable UUID channelId) {
    channelService.deleteChannel(channelId);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{channelId}")
  public ResponseEntity<ChannelDto> updateChannel(
      @PathVariable UUID channelId,
      @RequestBody PublicChannelUpdateRequest updateRequest) {
    ChannelDto updatedChannelDto = channelService.updatePublicChannel(channelId, updateRequest);
    return ResponseEntity.ok(updatedChannelDto);
  }

  @GetMapping
  public ResponseEntity<List<ChannelDto>> getAllChannelsByUserId(@RequestParam("userId") UUID userId) {
    List<ChannelDto> channelDtoList = channelService.findChannelListByUserId(userId);
    return ResponseEntity.ok(channelDtoList);
  }
}