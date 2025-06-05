package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.ArrayList;
import java.util.UUID;

public class JCFChannelService implements ChannelService {


    private static final JCFChannelService instance = new JCFChannelService();

    // 정적 대신 인스턴스 변수로 변경
    private final ArrayList<Channel> channelsData;

    private JCFChannelService() {
        channelsData = new ArrayList<>();
    }

    public static JCFChannelService getInstance() {
        return instance;
    }

    @Override
    public Channel createChannel(User user, String channelName) {
        Channel channel = new Channel(user, channelName);
        channelsData.add(channel);

        System.out.printf("채널 생성 - 채널 주인: %s, 채널 이름: %s, 채널 ID: %s%n",
                user.getId(), channelName, channel.getId());
        user.addChannel(channel);
        return channel;
    }


    @Override
    public void userJoinChannel(User user, Channel channel) {
        System.out.printf("'%s' 에 '%s' 이 입장했습니다.%n", channel.getChannelName(), user.getUserName());
        channel.addUser(user);
        user.addChannel(channel);
    }

    @Override
    public void userLeaveChannel(User user, Channel channel) {
        System.out.printf("'%s' 유저가 '%s' 채널을 떠났습니다.%n", user.getUserName(), channel.getChannelName());

        channel.removeUser(user);
        //user.removeChannel(channel); 오류 남
        if (channel.getUsers().isEmpty()) {
            System.out.printf("'%s' 채널은 유저 수가 0이므로 삭제합니다.%n", channel.getChannelName());
            channelsData.remove(channel);
        }
    }

    @Override
    public void printAllChannels() {
        System.out.printf("전체 채널 조회, 채널 수: %d%n", channelsData.size());
        channelsData.forEach(channel -> System.out.println(channel.getChannelName()));
    }

    @Override
    public void printChannel(Channel channel) {
        System.out.printf("채널 단일 조회, 채널 이름: %s%n", channel.getChannelName());
        System.out.println(channel.toString());
    }

    @Override
    public void printUsersFromChannel(Channel channel) {
        System.out.printf("%s 채널 내 유저 검색, 채널 내 유저 수: %d%n",
                channel.getChannelName(), channel.getUsers().size());

        channel.getUsers().forEach(u -> System.out.println(u.getUserName()));
    }

    @Override
    public void updateChannelName(User user, Channel channel, String newName) {
        if (channel.getHostUser().equals(user)) {
            System.out.printf("'%s' 채널의 이름이 '%s' 으로 변경되었습니다.%n",
                    channel.getChannelName(), newName);
            channel.updateChannelName(newName);
        } else {
            System.out.printf("'%s' 은 '%s' 채널 주인이 아닙니다.%n",
                    user.getUserName(), channel.getChannelName());
        }
    }

    @Override
    public void deleteChannel(User user, Channel channel) {
        if (!user.equals(channel.getHostUser())) {
            return;
        }

        System.out.printf("채널 삭제: %s%n", channel.getChannelName());
        channel.getMessages()
                .stream()
                .map(message -> {
                    message.getUser().removeMessage(message);
                    return message;
                });

        channel.getUsers()
                .stream()
                .map(u -> {
                    u.removeChannel(channel);
                    return u;
                });

        channel.clearUsers();
        channel.clearMessages();

        channelsData.remove(channel);
    }

    @Override
    public void updateHostUser(User oldHostUser, Channel channel, User newHostUser) {
        if (channel.getHostUser().equals(oldHostUser)) {
            System.out.printf("'%s' 채널 주인을 변경합니다. 새 주인: '%s'%n",
                    channel.getChannelName(), newHostUser.getUserName());
            channel.updateHostUser(oldHostUser);
        } else {
            System.out.printf("'%s' 은 '%s' 채널 주인이 아닙니다.%n",
                    oldHostUser.getUserName(), channel.getChannelName());
        }
    }
}
