package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserActivationState;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Repository
public class FileMessageRepository implements MessageRepository, Serializable {

    private static final String USER_FILE_PATH = "./data/message.ser";

    @Override
    public List<Message> loadMessages(){
        File file = new File(USER_FILE_PATH);

        if (!file.exists() || file.length() == 0) {
            return new ArrayList<Message>(); // 해당 파일이 없거나 비어 있다면 빈 ArrayList를 반환
        }
        // ArrayList<User> 역직렬화및 반환
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USER_FILE_PATH))) {
            return (List<Message>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public void saveMessages(List<Message> messages){
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USER_FILE_PATH))) {
            oos.writeObject(messages);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createMessage(Message message){
        //System.out.printf("메세지를 추가합니다. User: %s, ChannelId: %s, Contents: %s%n", user.getId(), channel.getId(), contents);
        List<Message> messages = loadMessages();
        messages.add(message);
        saveMessages(messages);
    }

    @Override
    public void deleteMessage(User user, Message message){
        List<Message> messagesFromFile = loadMessages();
        messagesFromFile.removeIf(msg -> msg.equalsId(message));
        saveMessages(messagesFromFile);
    }

    @Override
    public void updateMessage(User user, Message message, String newContents) {
        if (message == null) {
            //System.out.println("메세지가 null 입니다.");
            return;
        }
        if (user.getStatus().equals(UserActivationState.DEACTIVE)) {
            //System.out.printf("'%s' 비활성 상태라 메세지를 업데이트할 수 없습니다.", user.getUserName());
            return;
        }
        if (!user.getId().equals(message.getAuthorId())) {
            //System.out.printf("'%s'는 '%s' 메시지의 주인이 아닙니다. 따라서 해당 메시지를 '%s'로 바꾸는 것은 불가능합니다.", user.getUserName(), message.getMessageContents(), newContents);
            return;
        }

        List<Message> messagesFromFile = loadMessages();
        for (Message messageFromFile : messagesFromFile) {
            if (messageFromFile.equalsId(message)) {
                messageFromFile.updateMessageContent(newContents);
                break;
            }
        }
        saveMessages(messagesFromFile);
    }

    @Override
    public Message getMessageById(UUID messageId){
        return loadMessages().stream()
                .filter(msg -> msg.getId().equals(messageId))
                .findFirst()
                .orElse(null);
    }
}