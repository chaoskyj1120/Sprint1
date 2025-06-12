package com.sprint.mission.discodeit.참고;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileChannelRepository11 implements ChannelRepository {

    private static final String CHANNEL_FILE_PATH = "./data/channels.ser";
    private static final FileChannelRepository11 instance = new FileChannelRepository11();

    private FileChannelRepository11() {}

    public static FileChannelRepository11 getInstance() {
        return instance;
    }

    /**
     * 채널 목록을 파일에서 불러옵니다.
     */
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

    /**
     * 채널 목록을 파일에 저장합니다.
     */
    private void saveChannels(List<Channel> channels) {
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
        channels.add(channel);
        saveChannels(channels);

        return channel;
    }

    @Override
    public void addUserToChannel(User user, Channel channel) {
        if (user.getStatus() == UserStatus.DEACTIVE || channel == null) return;

        List<Channel> channels = loadChannels();
        for (Channel ch : channels) {
            if (ch.equals(channel) && !ch.getUsers().contains(user)) {
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
        for (Channel ch : channels) {
            if (ch.equals(channel) && ch.getHostUser().equals(user)) {
                ch.updateChannelName(newName);
                break;
            }
        }
        saveChannels(channels);
    }

    @Override
    public void deleteChannel(User user, Channel channel) {
        if (user.getStatus() == UserStatus.DEACTIVE || channel == null) return;

        List<Channel> channels = loadChannels();
        channels.removeIf(ch -> ch.equals(channel) && ch.getHostUser().equals(user));
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

    /**
     * 저장소에 있는 모든 채널을 반환합니다.
     */
    public List<Channel> getAllChannels() {
        return loadChannels();
    }
}