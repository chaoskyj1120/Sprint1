package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class FileChannelRepository implements ChannelRepository, Serializable {

    private static final String CHANNEL_FILE_PATH = "./data/channels.ser";

    private List<Channel> loadChannels() {
        File file = new File(CHANNEL_FILE_PATH);
        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Channel>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    public void saveChannels(List<Channel> channels) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CHANNEL_FILE_PATH))) {
            oos.writeObject(channels);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public Channel createChannel(User user, String channelName) {
        if (user.getStatus() == UserStatus.DEACTIVE) return null;

        List<Channel> channels = loadChannels();
        Channel channel = new Channel(user, channelName);

        channel.addUser(user);
        channels.add(channel);
        
        saveChannels(channels);

        return channel;
    }

    @Override
    public void deleteChannel(User user, Channel channel) {
        if (user.getStatus() == UserStatus.DEACTIVE || channel == null) return;

        List<Channel> channels = loadChannels();
        channels.removeIf(ch -> ch.getId().equals(channel.getId()) && ch.getHostUser().getId().equals(user.getId()));

        saveChannels(channels);
    }

    @Override
    public void addUserToChannel(User user, Channel channel) {
        if (user.getStatus() == UserStatus.DEACTIVE || channel == null) return;

        List<Channel> channels = loadChannels();
        for (Channel ch : channels) {
            if (ch.getId().equals(channel.getId()) &&
                    ch.getUsers().stream().noneMatch(u -> u.getId().equals(user.getId()))) {

                ch.addUser(user);
                break;
            }
        }

        saveChannels(channels);
    }

    @Override
    public void leaveUserFromChannel(User user, Channel channel) {
        if (user.getStatus() == UserStatus.DEACTIVE || channel == null) return;

        List<Channel> channels = loadChannels();
        channels.removeIf(ch -> {
            if (ch.equals(channel)) {
                ch.removeUser(user);
                return ch.getUsers().isEmpty(); // 유저 다 나가면 삭제
            }
            return false;
        });

        saveChannels(channels);
    }

    @Override
    public void updateChannelName(User user, Channel channel, String newName) {
        if (user.getStatus() == UserStatus.DEACTIVE || channel == null) return;

        List<Channel> channels = loadChannels();
        channels.stream()
                .filter(ch -> ch.getId().equals(channel.getId()) && ch.getHostUser().getId().equals(user.getId()))
                .findFirst()
                .ifPresent(ch -> ch.updateChannelName(newName));

        saveChannels(channels);
    }

    @Override
    public void updateHostUser(User oldHostUser, Channel channel, User newHostUser) {
        if (channel == null || oldHostUser.getStatus() == UserStatus.DEACTIVE || newHostUser.getStatus() == UserStatus.DEACTIVE)
            return;

        List<Channel> channels = loadChannels();
        for (Channel ch : channels) {
            if (ch.equals(channel) && ch.getHostUser().equals(oldHostUser) && ch.getUsers().contains(newHostUser)) {
                ch.updateHostUser(newHostUser);
                break;
            }
        }

        saveChannels(channels);
    }

    @Override
    public List<Channel> getAllChannels() {
        return loadChannels();
    }

}
