package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.util.ArrayList;

public class FileMessageRepository implements MessageRepository, Serializable {

    private static final FileMessageRepository instance = new FileMessageRepository();
    private final ArrayList<Message> data;

    public FileMessageRepository() {
        this.data = loadMessages();
    }
    public ArrayList<Message> getMessages() {
        return data;
    }
    public static FileMessageRepository getInstance() {
        return instance;
    }

    @Override
    public void saveMessages(){
        System.out.println("메세지 리스트 저장");
        String filePath = "./data/messages.ser";

        // ArrayList<Message> 직렬화및 저장
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(data);
            System.out.println("Message 리스트가 직렬화되어 '" + filePath + "' 파일에 저장되었습니다.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<Message> loadMessages(){
        ArrayList<Message> deserializedMessages = null;
        System.out.println("메세지 리스트 불러오기");
        String filePath = "./data/messages.ser"; // 유저 직렬화

        if (!new File(filePath).exists() || new File(filePath).length() == 0) {
            return new ArrayList<Message>(); // 해당 파일이 없으면 빈 ArrayList를 반환
        }

        // ArrayList<Message> 역직렬화및 반환
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            deserializedMessages = (ArrayList<Message>) ois.readObject();
            /*
            System.out.println("역직렬화된 Message 리스트 정보:");

            for (Message message : deserializedMessages) {
                System.out.println("------------");
                System.out.println("Message: " + message.toString());
            }*/

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return deserializedMessages;
    }
}
