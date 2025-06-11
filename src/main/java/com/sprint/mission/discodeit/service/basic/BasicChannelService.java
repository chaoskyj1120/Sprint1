package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;

import java.io.*;
import java.util.ArrayList;

public class BasicChannelService implements ChannelService {

    private static final BasicChannelService instance = new BasicChannelService();
    private final FileChannelRepository fileChannelRepository;
    private final ArrayList<Channel> data; // channelData
    // data를 많이 참조하고 있는데 본 클래스에서 data를 못 다루는건 문제가 나중에 생길지도?

    public BasicChannelService() {
        fileChannelRepository = FileChannelRepository.getInstance();
        data = fileChannelRepository.getChannels();
    }

    public static BasicChannelService getInstance() {
        return instance;
    }

    @Override
    public Channel createChannel(User user, String channelName) {
        return fileChannelRepository.createChannel(user, channelName);
    }

    @Override
    public void addUserToChannel(User user, Channel channel) {
        fileChannelRepository.addUserToChannel(user, channel);
    }

    @Override
    public void leaveUserFromChannel(User user, Channel channel) {
        fileChannelRepository.leaveUserFromChannel(user, channel);
    }

    @Override
    public void updateChannelName(User user, Channel channel, String newName) {
        fileChannelRepository.updateChannelName(user, channel, newName);
    }

    @Override
    public void deleteChannel(User user, Channel channel) {
        fileChannelRepository.deleteChannel(user, channel);
    }

    @Override
    public void updateHostUser(User oldHostUser, Channel channel, User newHostUser) {
        fileChannelRepository.updateHostUser(oldHostUser, channel, newHostUser);
    }

    @Override
    public void printAllChannels() {
        System.out.printf("전체 채널 조회, 채널 수: %d%n", data.size());
        data.forEach(channel -> System.out.println(channel.getChannelName()));
    }

    @Override
    public void printChannel(Channel channel) {
        if(channel == null) {
            System.out.println("채널이 null 입니다.");
            return;
        }
        System.out.printf("채널 단일 조회, 채널 이름: %s, 채널 내 유저 수 %d%n", channel.getChannelName(), channel.getUsers().size());
        System.out.printf("채절 주인: '%s', 채널 내 유저 들=%s%n", channel.getHostUser().getUserName(), channel.getUsers());
        //System.out.println(channel.toString());
    }

    @Override
    public void printUsersFromChannel(Channel channel) {
        if(channel == null) {
            System.out.println("채널이 null 입니다.");
            return;
        }
        System.out.printf("%s 채널 내 유저 검색, 채널 내 유저 수: %d%n",
                channel.getChannelName(), channel.getUsers().size());

        channel.getUsers().forEach(u -> System.out.println(u.getUserName()));
    }

}
