package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.util.UUID;

@Getter
public class Message extends BaseEntity implements Serializable {

    private String messageContents;
    private UUID authorId;
    private UUID channelId;

    public Message(User user, Channel channel, String contents) {
        super();
        this.messageContents = contents;
        this.authorId = user.getId();
        this.channelId = channel.getId();
    }

    public void updateMessageContent(String messageContents) {
        updateUpdatedAt();
        this.messageContents = messageContents;
    }

    public void registerMessageToUserAndChannel(User user, Channel channel) {
        user.addMessage(this);
        channel.addMessage(this);
    }
}
