package com.sprint.mission.discodeit.dto.data;

import com.sprint.mission.discodeit.entity.Notification;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationDto {
  private final UUID id;
  private final Instant createdAt;
  private final UUID receiverId;
  private final String title;
  private final String content;

  public NotificationDto(Notification notification) {
    this.id = notification.getId();
    this.createdAt = notification.getCreatedAt();
    this.receiverId = notification.getReceiverId();
    this.title = notification.getTitle();
    this.content = notification.getContent();
  }

  public NotificationDto(UUID id, Instant createdAt, UUID receiverId, String title, String content) {
    this.id = id;
    this.createdAt = createdAt;
    this.receiverId = receiverId;
    this.title = title;
    this.content = content;
  }
}