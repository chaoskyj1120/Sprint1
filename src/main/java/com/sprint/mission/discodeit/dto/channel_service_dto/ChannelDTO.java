package com.sprint.mission.discodeit.dto.channel_service_dto;

import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import lombok.Getter;

import java.time.Instant;
import java.util.*;

@Getter
public class ChannelDTO {
    private final MessageRepository messageRepository = new FileMessageRepository();

    private final UUID channelId;
    private final String channelName;
    private final UUID hostUserId;
    private final ChannelType channelType;
    private final String channelDescription;

    private final Instant lastMessageTime;
    private final UUID lastMessageId; // 추가된 필드

    private final Set<UUID> userIds;
    private final Set<UUID> messageIds;

    public ChannelDTO(Channel channel) {
        this.channelId = channel.getId();
        this.channelName = channel.getChannelName();
        this.hostUserId = channel.getHostUserId();
        this.channelType = channel.getChannelType();
        this.channelDescription = channel.getChannelDescription();
        this.userIds = channel.getUserIds();
        this.messageIds = channel.getMessageIds();

        Instant lastMessageTime = null;
        UUID lastMessageId = null;

        if (!messageIds.isEmpty()) {
            List<Message> messagesInChannel = messageRepository.findMessagesByMessageIds(messageIds);

            Optional<Message> latestMessage = messagesInChannel.stream()
                    .max(Comparator.comparing(Message::getCreatedAt));

            if (latestMessage.isPresent()) {
                lastMessageTime = latestMessage.get().getCreatedAt();
                lastMessageId = latestMessage.get().getId();
            }
        }

        this.lastMessageTime = lastMessageTime;
        this.lastMessageId = lastMessageId;
    }
}
