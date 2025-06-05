package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;

public class User extends Parents{

    private String userName;

    private ArrayList<Channel> channels;
    private ArrayList<Message> messages;

    public User(String userName) {
        super();
        this.userName = userName;
        this.channels = new ArrayList<>();
        this.messages = new ArrayList<>();
    }

    public String getUserName() {
        return userName;
    }

    public void reUserName(String newUserName) {
        updateUpdatedAt(); // 메소드 쓰지 말고 필드 그대로
        this.userName = newUserName;
    }

    public ArrayList<Channel> getChannels() {
        return channels;
    }

    public void addChannel(Channel channel) {
        if(!channels.contains(channel)) {
            channels.add(channel);
            channel.addUser(this);
        }
    }

    public void removeChannel(Channel channel) {
        if(channels.contains(channel)) {
            channels.remove(channel);
            channel.removeUser(this);
        }
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void addMessage(Message message) {
        if(!this.messages.contains(message)) {
            this.messages.add(message);
        }
    }

    public void removeMessage(Message message) {
        if(messages.contains(message)) {
            this.messages.remove(message);
            message.getChannel().getMessages().remove(message);
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + getId() +
                ", userName='" + userName + '\'' +
                //", createdAt=" + getCreatedAt() +
                //", updatedAt=" + getUpdatedAt() +
                ", channels=" + channels.stream().map(channel -> channel.getId()).toList() +
                '}';
    }
}
