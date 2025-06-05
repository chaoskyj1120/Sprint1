package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.ArrayList;

public class JCFMessageService implements MessageService {

    private static JCFMessageService instance = new JCFMessageService();
    private final ArrayList<Message> messagesData;

    private JCFMessageService() {
        messagesData = new ArrayList<>();
    }

    public static JCFMessageService getInstance() {
        return instance;
    }

    @Override
    public Message createMessage(User user, Channel channel, String contents) {
        System.out.printf("메세지를 추가합니다. User: %s, ChannelId: %s, Contents: %s%n",
                user.getId(), channel.getId(), contents);
        Message newMessage = new Message(user, channel, contents);
        messagesData.add(newMessage);
        newMessage.registerMessageToUserAndChannel(user, channel);
        return newMessage;
    }

    @Override
    public void deleteMessage(User user, Message message) {
        if (user.getId().equals(message.getUser().getId())) {
            System.out.printf("메세지를 삭제합니다. User: %s, MessageId: %s, Contents: %s%n",
                    user.getUserName(), message.getId(), message.getMessageContents());
        } else {
            System.out.printf("'%s' 은 '%s' 메시지 주인이 아닙니다%n",
                    user.getUserName(), message.getId());
            return;
        }
        message.getChannel().getMessages().remove(message); // 채널에서 메세지 삭제
        user.removeMessage(message);
        messagesData.remove(message);
    }

    @Override
    public void updateMessage(User user, Message message, String newContents) {
        System.out.println("메세지를 업데이트 합니다.");
        System.out.printf("이전 메세지: %s%n", message.getMessageContents());
        System.out.printf("현재 메세지: %s%n", newContents);
        message.updateMessageContent(newContents);
    }

    @Override
    public void printMessagesByChannel(Channel channel) {
        System.out.printf("%s 채널의 메세지를 조회합니다. 메시지 수: %d%n",
                channel.getChannelName(), channel.getMessages().size());
        messagesData.stream()
                .filter(message -> message.getChannel().equals(channel))
                .forEach(message -> System.out.printf("작성자: %s, 내용: %s%n",
                        message.getUser().getUserName(), message.getMessageContents()));
    }

    @Override
    public void printMessage(Message message) {
        System.out.printf("메세지 ID: %s 의 정보를 출력합니다.%n", message.getId());
        System.out.println(message.toString());
    }

    @Override
    public void printAllMessage() {
        System.out.printf("전체 메시지를 출력합니다. 메세지 수: %d%n", messagesData.size());
        messagesData.forEach(message -> System.out.printf("작성자: %s, 내용: %s%n",
                message.getUser().getUserName(), message.getMessageContents()));
    }

    @Override
    public void printAllMessageByUser(User user) {
        System.out.printf("%s이 작성한 모든 메세지를 출력합니다. 메시지 수: %d%n",
                user.getUserName(), user.getMessages().size());
        user.getMessages().forEach(message ->
                System.out.printf("내용: %s%n", message.getMessageContents()));
    }
}
