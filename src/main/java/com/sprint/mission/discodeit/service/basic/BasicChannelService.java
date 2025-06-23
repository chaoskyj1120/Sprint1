package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;


    @Override
    public Channel createChannel(User user, String channelName) {
        if (user.getStatus() == UserActivationState.DEACTIVE) return null;

        Channel channel = new Channel(user, channelName);
        channel.addUser(user);

        List<User> usersFromFile = userRepository.loadUsers();
        for (User userFromFile : usersFromFile) {
            if(userFromFile.equalsId(user)) {
                userFromFile.addChannel(channel);
                break;
            }
        }

        channelRepository.createChannel(channel);
        userRepository.saveUsers(usersFromFile);

        return channelRepository.getChannelById(channel.getId());
    }

    @Override
    public void updateChannelName(User user, Channel channel, String newName) {
        channelRepository.updateChannelName(user, channel, newName);
    }

    @Override
    public void deleteChannel(User user, Channel channel) {
        if (user.getStatus() == UserActivationState.DEACTIVE || channel == null) return;

        if (!channel.getHostUserId().equals(user.getId())) {
            System.out.println("채널을 삭제할 권한이 없습니다.");
            return;
        }

        // 채널에서 유저 제거 같은 비즈니스 로직 수행
        List<User> users = userRepository.loadUsers();
        for (User u : users) {
            u.removeChannel(channel);
            u.getMessageIds().removeIf(messageId -> channel.getMessageIds().contains(messageId));
        }

        List<Message> messagesFromFile = messageRepository.loadMessages();
        messagesFromFile.removeIf(messageFromFile -> channel.getMessageIds().contains(messageFromFile.getId()));
        messageRepository.saveMessages(messagesFromFile);

        userRepository.saveUsers(users);
        channelRepository.deleteChannel(channel);
    }

    @Override
    public void addUserToChannel(User user, Channel channel) {
        if (user.getStatus() == UserActivationState.DEACTIVE){
            System.out.println("유저 상태가 비활성입니다.");
            return;
        }

        if (channel == null) {
            System.out.println("채널이 Null 입니다.");
            return;
        }

        List<User> usersFromFile = userRepository.loadUsers();
        for (User userFromFile : usersFromFile) {
            if (userFromFile.equalsId(user)) {
                userFromFile.addChannel(channel); // 채널 추가
                break; // 유저를 찾았으니 루프 종료
            }
        }

        userRepository.saveUsers(usersFromFile);
        channelRepository.addUserToChannel(user, channel);
    }

    @Override
    public void leaveUserFromChannel(User user, Channel channel) {
        if (user.getStatus() == UserActivationState.DEACTIVE || channel == null) return;
        List<User> usersFromFile = userRepository.loadUsers();

        for (User userFromFile : usersFromFile) {
            if (userFromFile.equalsId(user)) {
                userFromFile.removeChannel(channel);
            }
        }

        userRepository.saveUsers(usersFromFile);
        channelRepository.leaveUserFromChannel(user, channel);
    }


    @Override
    public void updateHostUser(User oldHostUser, Channel channel, User newHostUser) {
        channelRepository.updateHostUser(oldHostUser, channel, newHostUser);
    }

    @Override
    public void printAllChannels() {
        List<Channel> channels = channelRepository.loadChannels();
        System.out.printf("전체 채널 조회, 채널 수: %d%n", channels.size());
        channels.forEach(channel -> System.out.printf("채널 명: %s, 채널 ID: %s, 채널내 유저 수: %d%n", channel.getChannelName(), channel.getId(), channel.getUserIds().size()));
    }

    @Override
    public void printChannel(Channel channel) {
        if(channel == null) {
            System.out.println("채널이 null 입니다.");
            return;
        }
        System.out.printf("채널 단일 조회, 채널 이름: %s, 채널 내 유저 수 %d%n", channel.getChannelName(), channel.getUserIds().size());
        System.out.printf("채절 주인: '%s', 채널 내 유저 들=%s%n", channel.getHostUserId(), channel.getUserIds());
        //System.out.println(channel.toString());
    }

    @Override
    public void printUsersFromChannel(Channel channel) {
        if(channel == null) {
            System.out.println("채널이 null 입니다.");
            return;
        }
        System.out.printf("%s 채널 내 유저 검색, 채널 내 유저 수: %d%n",
                channel.getChannelName(), channel.getUserIds().size());

        channel.getUserIds().forEach(System.out::println);
    }


    public Channel enterChanner(User user, Channel chanel){
        // 채널에 가입된 유저가 채널에 입장했을 떄 / 가입이랑 다름
        // 채널을 반환해야 하나?
        // TODO ReadStatus를 추가 또는 변경

        return null;
    }

    public void exitChanner(User user, Channel channel){
        // 채널에 가입된 유저가 채널을 퇴장했을 때 / 탈퇴랑 다름
        // 퇴장할 떄 업데이터 해야 함
        updateReadStatus(user, channel);
    }

    public void updateReadStatus(User user, Channel channel) {
        List<ReadStatus> readStatuses = readStatusRepository.loadReadStatuses();
        Set<UUID> messageIds = channel.getMessageIds();
        UUID lastMessageId = messageIds.isEmpty() ? null : new ArrayList<>(messageIds).get(messageIds.size() - 1);

        Optional<ReadStatus> matchedReadStatus = readStatuses.stream()
                .filter(readStatus -> readStatus.equalsId(user) && readStatus.equalsId(channel))
                .findFirst();

        ReadStatus targetReadStatus;

        if (matchedReadStatus.isEmpty()) {
            targetReadStatus = new ReadStatus(user.getId(), channel.getHostUserId());
            readStatuses.add(targetReadStatus); // 새로 만든 ReadStatus를 리스트에 추가
            readStatusRepository.createReadStatus(targetReadStatus); // 저장소에도 생성
        } else {
            targetReadStatus = matchedReadStatus.get();
        }

        targetReadStatus.setMessageId(lastMessageId);

        readStatusRepository.saveReadStatuses(readStatuses); // 전체 리스트 저장
    }

}
