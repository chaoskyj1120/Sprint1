package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class FileMessageRepository implements MessageRepository, Serializable {

    private static final String USER_FILE_PATH = "./data/message.ser";

    private List<Message> loadMessages(){
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

    public void saveMessages(List<Message> messages){
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USER_FILE_PATH))) {
            oos.writeObject(messages);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Message createMessage(User user, Channel channel, String contents){
        if(channel == null) {
            //System.out.println("채널이 null 입니다.");
            return null;
        }
        if(user.getStatus().equals(UserStatus.DEACTIVE)) {
            //System.out.printf("'%s' 비활성 상태라 메세지를 작성할 수 없습니다.", user.getUserName());
            return null;
        }
        //System.out.printf("메세지를 추가합니다. User: %s, ChannelId: %s, Contents: %s%n", user.getId(), channel.getId(), contents);
        List<Message> messages = loadMessages();
        Message newMessage = new Message(user, channel, contents);
        newMessage.registerMessageToUserAndChannel(user, channel);
        messages.add(newMessage);

        saveMessages(messages);
        return newMessage;
    }

    @Override
    public void deleteMessage(User user, Message message){
        if(message == null) {
            return;
        }
        if(user.getStatus().equals(UserStatus.DEACTIVE)) {
            return;
        }
        if (!user.getId().equals(message.getUser().getId())) {
            return;
        }
        List<Message> messagesFromFile = loadMessages();
        messagesFromFile.remove(message);
        for( Message messageFromFile : messagesFromFile) {
            if(messageFromFile.getId().equals(message.getId())) {
                messagesFromFile.remove(messageFromFile);
                break;
            }
        }

        saveMessages(messagesFromFile);
    }

    @Override
    public void updateMessage(User user, Message message, String newContents){
        if(message == null) {
            //System.out.println("메세지가 null 입니다.");
            return;
        }
        if(user.getStatus().equals(UserStatus.DEACTIVE)) {
            //System.out.printf("'%s' 비활성 상태라 메세지를 업데이트할 수 없습니다.", user.getUserName());
            return;
        }
        if(!user.equals(message.getUser())) {
            //System.out.printf("'%s'는 '%s' 메시지의 주인이 아닙니다. 따라서 해당 메시지를 '%s'로 바꾸는 것은 불가능합니다.", user.getUserName(), message.getMessageContents(), newContents);
            return;
        }

        List<Message> messagesFromFile = loadMessages();
        for (Message messageFromFile : messagesFromFile) {
            if(messageFromFile.getId().equals(message.getId())) {
                messageFromFile.updateMessageContent(newContents);
                break;
            }
        }

        saveMessages(messagesFromFile);
    }

    @Override
    public List<Message> getMessages() {
        return loadMessages();
    }

}
