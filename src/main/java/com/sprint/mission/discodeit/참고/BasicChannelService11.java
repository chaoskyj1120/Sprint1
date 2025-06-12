package com.sprint.mission.discodeit.참고;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;

/**
 * ChannelService 구현체
 * - 비즈니스 로직만 담당하고, 저장/불러오기 등의 영속화는 ChannelRepository에 위임
 */
public class BasicChannelService11 implements ChannelService {

    private final ChannelRepository channelRepository;

    /**
     * 생성자 주입 방식으로 저장소 주입
     * (이 방식은 추후 테스트 시 mock 주입이 쉬움)
     */
    public BasicChannelService11(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    /**
     * 채널 생성 요청을 저장소에 위임
     */
    @Override
    public Channel createChannel(User user, String channelName) {
        return channelRepository.createChannel(user, channelName);
    }

    /**
     * 유저를 채널에 추가하는 로직을 저장소에 위임
     */
    @Override
    public void addUserToChannel(User user, Channel channel) {
        channelRepository.addUserToChannel(user, channel);
    }

    /**
     * 유저를 채널에서 제거하는 로직을 저장소에 위임
     */
    @Override
    public void leaveUserFromChannel(User user, Channel channel) {
        channelRepository.leaveUserFromChannel(user, channel);
    }

    /**
     * 채널 이름 수정 요청 위임
     */
    @Override
    public void updateChannelName(User user, Channel channel, String newName) {
        channelRepository.updateChannelName(user, channel, newName);
    }

    /**
     * 채널 삭제 로직 위임
     */
    @Override
    public void deleteChannel(User user, Channel channel) {
        channelRepository.deleteChannel(user, channel);
    }

    /**
     * 채널 주인 변경 로직 위임
     */
    @Override
    public void updateHostUser(User oldHostUser, Channel channel, User newHostUser) {
        channelRepository.updateHostUser(oldHostUser, channel, newHostUser);
    }

    /**
     * 전체 채널 출력 (Repository가 제공해야 할 책임이지만, 현재는 Service에서 직접 사용)
     */
    @Override
    public void printAllChannels() {
        System.out.println("전체 채널은 저장소에서 직접 조회해야 합니다. 해당 기능은 Repository에 위임되어야 합니다.");
        // 필요한 경우 channelRepository.getAllChannels() 메서드를 추가할 것
        List<Channel> channels = channelRepository.getAllChannels();
        for (Channel channel : channels) {
            System.out.println(channel);
        }
    }

    /**
     * 단일 채널 출력
     */
    @Override
    public void printChannel(Channel channel) {
        if(channel == null) {
            System.out.println("채널이 null 입니다.");
            return;
        }
        System.out.printf("채널 단일 조회, 채널 이름: %s, 채널 내 유저 수: %d%n",
                channel.getChannelName(), channel.getUsers().size());
        System.out.printf("채널 주인: '%s', 채널 내 유저들: %s%n",
                channel.getHostUser().getUserName(), channel.getUsers());
    }

    /**
     * 채널 내 유저 출력
     */
    @Override
    public void printUsersFromChannel(Channel channel) {
        if(channel == null) {
            System.out.println("채널이 null 입니다.");
            return;
        }
        System.out.printf("%s 채널 내 유저 검색, 유저 수: %d%n",
                channel.getChannelName(), channel.getUsers().size());
        channel.getUsers().forEach(user -> System.out.println(user.getUserName()));
    }
} 
