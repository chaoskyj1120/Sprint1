package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
public class Channel extends BaseEntity implements Serializable {
    private String channelName;
    private UUID hostUserId;

    private final Set<UUID> messageIds = new HashSet<>();
    private final Set<UUID> userIds  = new HashSet<>();

    public Channel(User hostUser, String name) {
        super();
        this.channelName = name;
        this.hostUserId = hostUser.getId();
    }

    public void addUser(User user) {
        if (!userIds.add(user.getId())) {
            System.out.println("User: " + user.getId() + ", already exists!");
            return;
        }
        user.addChannel(this);
    }

    public void removeUser(User user) {
        if (!userIds.remove(user.getId())) {
            System.out.println("User: " + user.getId() + ", isn't exists!");
            return;
        }
        user.removeChannel(this);
    }

    public void addMessage(Message message) {
        if (!messageIds.add(message.getId())) {
            System.out.println("Message: " + message.getId() + ", already exists!");
        }
    }

    public void removeMessage(Message message) {
        if (!messageIds.remove(message.getId())) {
            System.out.println("Message: " + message.getId() + ", isn't exists!");
        }
    }

    public void updateChannelName(String channelName) {
        super.updateUpdatedAt();
        this.channelName = channelName;
    }

    public void updateHostUser(User newHostUser) {
        updateUpdatedAt();
        this.hostUserId = newHostUser.getId();
    }

    public void clearUserIds(){
        userIds.clear();
    }

    public void clearMessageIds() {
        messageIds.clear();
    }
}