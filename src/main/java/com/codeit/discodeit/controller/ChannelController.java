package com.codeit.discodeit.controller;


import com.codeit.discodeit.mapper.ChannelMapper;
import com.codeit.discodeit.dto.channel_service_dto.*;
import com.codeit.discodeit.entity.Channel;
import com.codeit.discodeit.entity.Message;
import com.codeit.discodeit.entity.ReadStatus;
import com.codeit.discodeit.mapper.UserMapper;
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
  private final ChannelMapper channelMapper;
  private final UserMapper userMapper;

  @PostMapping(value = "/public", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ChannelDto> createPublicChannel(
      @RequestBody CreatePublicChannelRequestDto createPublicChannelRequestDto) {
    Channel channel = channelService.createPublicChannel(channelMapper.toPublicChannel(createPublicChannelRequestDto));
    ChannelDto channelDto = toChannelDto(channel);
    return ResponseEntity.status(HttpStatus.CREATED).body(channelDto);
  }


  @PostMapping(value = "/private", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ChannelDto> createPrivateChannel(
      @RequestBody PrivateChannelCreateRequest privateChannelCreateRequest) {
    Channel channel = channelService.createPrivateChannel(channelMapper.toPrivateChannel(), privateChannelCreateRequest.getParticipantIds());
    ChannelDto channelDto = toChannelDto(channel);
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
    Channel channel = channelService.updatePublicChannel(channelId, updateRequest);
    ChannelDto updatedChannelDto = toChannelDto(channel);
    return ResponseEntity.ok(updatedChannelDto);
  }

  @GetMapping
  public ResponseEntity<List<ChannelDto>> getAllChannelsByUserId(@RequestParam("userId") UUID userId) {
    List<Channel> channelList = channelService.findChannelListByUserId(userId);
    List<ChannelDto> channelDtoList = channelList.stream().map(this::toChannelDto).toList();
    return ResponseEntity.ok(channelDtoList);
  }

  private ChannelDto toChannelDto(Channel channel) {
    Optional<Message> lastMessage = messageService.findLastMessageInChannel(channel.getId());
    List<ReadStatus> readStatuses = readStatusService.findReadStatusesByChannelId(channel);
    return channelMapper.toChannelDto(channel, lastMessage, readStatuses, userMapper);
  }
}