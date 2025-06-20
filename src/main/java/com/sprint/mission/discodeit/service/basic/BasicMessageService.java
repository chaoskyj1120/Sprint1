package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserActivationState;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {

    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;

    @Override
    public Message createMessage(User user, Channel channel, String contents) {
        if(channel == null) {
            //System.out.println("채널이 null 입니다.");
            return null;
        }
        if(user.getStatus().equals(UserActivationState.DEACTIVE)) {
            //System.out.printf("'%s' 비활성 상태라 메세지를 작성할 수 없습니다.", user.getUserName());
            return null;
        }

        Message newMessage = new Message(user, channel, contents);
        newMessage.registerMessageToUserAndChannel(user, channel);
        messageRepository.createMessage(newMessage);

        List<User> usersFromFile = userRepository.loadUsers();
        usersFromFile.stream().filter(userFromFile -> userFromFile.equalsId(user))
                .forEach(userFromFile -> userFromFile.addMessage(newMessage));

        List<Channel> channelsFromFile = channelRepository.loadChannels();
        channelsFromFile.stream().filter(channelFromFile -> channelFromFile.equalsId(channel))
                .forEach(channelFromFile -> channelFromFile.addMessage(newMessage));


        return messageRepository.getMessageById(newMessage.getId());
    }

    @Override
    public void deleteMessage(User user, Message message) {
        if(message == null) {
            return;
        }
        if(user.getStatus().equals(UserActivationState.DEACTIVE)) {
            return;
        }
        if (!user.getId().equals(message.getId())) {
            return;
        }

        List<User> usersFromFile = userRepository.loadUsers();
        List<Channel> channelsFromFile = channelRepository.loadChannels();

        usersFromFile.forEach(userFromFile -> userFromFile.getMessageIds()
                .removeIf(messageId -> messageId.equals(message.getId())));

        channelsFromFile.forEach(channelFromFile -> channelFromFile.getMessageIds().
                removeIf(messageId -> messageId.equals(message.getId())));


        userRepository.saveUsers(usersFromFile);
        channelRepository.saveChannels(channelsFromFile);

        messageRepository.deleteMessage(user, message);
    }

    @Override
    public void updateMessage(User user, Message message, String newContents) {
        messageRepository.updateMessage(user, message, newContents);
    }

    @Override
    public void printMessagesByChannel(Channel channel) {
        if(channel == null) {
            System.out.println("채널이 null 입니다.");
            return;
        }
        List<Message> messagesFromFile = messageRepository.loadMessages();
        System.out.printf("%s 채널의 메세지를 조회합니다. 메시지 수: %d%n",
                channel.getChannelName(), channel.getMessageIds().size());

        messagesFromFile.stream()
                .filter(message -> channel.getMessageIds().contains(message.getId()))
                .forEach(message -> System.out.printf("작성자: %s, 내용: %s%n",
                        message.getAuthorId(), message.getMessageContents()));
    }

    @Override
    public void printMessage(Message message) {
        if(message == null) {
            System.out.println("메세지가 null 입니다.");
            return;
        }
        System.out.printf("메세지 ID: %s 의 정보를 출력합니다.%n", message.getId());
        System.out.println(message.toString());
    }

    @Override
    public void printAllMessage() {
        List<Message> messageFromFile = messageRepository.loadMessages();
        System.out.printf("전체 메시지를 출력합니다. 메세지 수: %d%n", messageFromFile.size());

        messageFromFile
                .forEach(message -> System.out.printf("작성자: %s, 내용: %s%n", message.getAuthorId(), message.getMessageContents()));
    }

    @Override
    public void printAllMessageByUser(User user) {
        // 이렇게 하면 최신 저장된 메시지가 출력되지는 않을 것 같은데
        System.out.printf("%s이 작성한 모든 메세지를 출력합니다. 메시지 수: %d%n",
                user.getUserName(), user.getMessageIds().size());
        user.getMessageIds()
                .forEach(message -> System.out.printf("메시지: %s%n", message));
    }
}
