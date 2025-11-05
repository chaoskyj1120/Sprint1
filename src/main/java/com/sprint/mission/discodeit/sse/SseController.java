package com.sprint.mission.discodeit.sse;

import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SseController {

  private final SseService sseService;

  @GetMapping(value = "/api/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter connect(
      Authentication authentication,
      @RequestHeader(name = "Last-Event-ID", required = false) String lastEventIdHeader
  ) {
    DiscodeitUserDetails user = (DiscodeitUserDetails) authentication.getPrincipal();
    UUID receiverId = user.getUserDto().id();   // <- 여기서 UUID 가져온다고 가정

    UUID lastEventId = null;
    if (lastEventIdHeader != null && !lastEventIdHeader.isBlank()) {
      try {
        lastEventId = UUID.fromString(lastEventIdHeader);
      } catch (IllegalArgumentException e) {
        log.warn("Invalid Last-Event-ID header: {}", lastEventIdHeader);
      }
    }

    log.info("SSE connect: receiverId={}, lastEventId={}", receiverId, lastEventId);

    SseEmitter emitter = sseService.connect(receiverId, lastEventId);
    return emitter;
  }
}
