package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserActivationState;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Repository
public class FileChannelRepository implements ChannelRepository, Serializable {

    private static final String CHANNEL_FILE_PATH = "./data/channels.ser";

    @Override
    public List<Channel> loadChannels() {
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

    @Override
    public void saveChannels(List<Channel> channels) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CHANNEL_FILE_PATH))) {
            oos.writeObject(channels);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createChannel(Channel channel) {
        List<Channel> channels = loadChannels();
        channels.add(channel);
        saveChannels(channels);
    }

    @Override
    public void deleteChannel(Channel channel) {
        List<Channel> channels = loadChannels();
        channels.removeIf(ch -> ch.equalsId(channel));
        saveChannels(channels);
    }

    @Override
    public void addUserToChannel(User user, Channel channel) {
        List<Channel> channels = loadChannels();

        channels.stream()
                .filter(ch -> ch.equalsId(channel))
                .findFirst()
                .ifPresent(ch -> ch.addUser(user));

        saveChannels(channels);
    }

    @Override
    public void leaveUserFromChannel(User user, Channel channel) {
        List<Channel> channels = loadChannels();

        channels.stream()
                .filter(channelFromFile -> channelFromFile.equalsId(channel))
                .forEach(channelFromFile -> channelFromFile.removeUser(user));

        saveChannels(channels);
    }

    @Override
    public void updateChannelName(User user, Channel channel, String newName) {
        if (user.getStatus() == UserActivationState.DEACTIVE || channel == null) return;

        List<Channel> channels = loadChannels();
        for (Channel channelFromFile : channels) {
            if (channelFromFile.equalsId(channel)) {
                channelFromFile.updateChannelName(newName);
                break;
            }
        }

        saveChannels(channels);
    }

    @Override
    public void updateHostUser(User oldHostUser, Channel channel, User newHostUser) {
        if (channel == null || oldHostUser.getStatus() == UserActivationState.DEACTIVE || newHostUser.getStatus() == UserActivationState.DEACTIVE)
            return;

        List<Channel> channels = loadChannels();

        for (Channel channelFromFile : channels) {
            if (channelFromFile.equalsId(channel)) {
                channelFromFile.updateHostUser(oldHostUser);
                break;
            }
        }
        saveChannels(channels);
    }

    @Override
    public Channel getChannelById(UUID channelId){
        return loadChannels().stream()
                .filter(ch -> ch.getId().equals(channelId))
                .findFirst()
                .orElse(null);
    }
}