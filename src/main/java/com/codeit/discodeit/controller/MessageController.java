package com.codeit.discodeit.controller;

import com.codeit.discodeit.mapper.MessageMapper;
import com.codeit.discodeit.dto.message_service_dto.DeleteMessageRequestDto;
import com.codeit.discodeit.dto.message_service_dto.MessageCreateRequest;
import com.codeit.discodeit.dto.message_service_dto.MessageDto;
import com.codeit.discodeit.dto.message_service_dto.MessageUpdateRequest;
import com.codeit.discodeit.dto.response.PageResponse;
import com.codeit.discodeit.dto.response.Pageable;
import com.codeit.discodeit.service.ChannelService;
import com.codeit.discodeit.service.MessageService;
import com.codeit.discodeit.service.UserService;
import com.codeit.discodeit.swagger.SwaggerMessageController;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Tag(name = "Message", description = "Message API")
@RequestMapping("/api/messages")
public class MessageController implements SwaggerMessageController {

  private final MessageService messageService;
  private final MessageMapper messageMapper;
  private final UserService userService;
  private final ChannelService channelService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<MessageDto> createMessage(
      @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
  ) throws IOException {

    MessageDto messageDto = messageService.createMessage(messageCreateRequest, attachments);
    return ResponseEntity.status(HttpStatus.CREATED).body(messageDto);
  }

  @GetMapping
  public ResponseEntity<PageResponse<MessageDto>> findAllByChannelId(
      @RequestParam("channelId") UUID channelId,
      @PageableDefault(size = 50, page = 0, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
    PageResponse<MessageDto> pageResponse = messageService.findMessagesPerPage(channelId, pageable);
    return ResponseEntity.ok(pageResponse);
  }

  @DeleteMapping("/{messageId}")
  public ResponseEntity<Void> deleteMessage(
      @PathVariable("messageId") UUID messageId) {
    DeleteMessageRequestDto requestDto = new DeleteMessageRequestDto();
    requestDto.setMessageId(messageId);
    messageService.deleteMessage(requestDto);

    return ResponseEntity.noContent().build(); // HTTP 204
  }

  @PatchMapping("/{messageId}")
  public ResponseEntity<MessageDto> updateMessage(
      @PathVariable("messageId") UUID messageId,
      @RequestBody MessageUpdateRequest messageUpdateRequest) {
    MessageDto messageDto = messageService.updateMessage(messageId, messageUpdateRequest);

    return ResponseEntity.ok(messageDto);
  }
}
