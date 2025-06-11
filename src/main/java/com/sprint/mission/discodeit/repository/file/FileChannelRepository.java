package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.util.ArrayList;

public class FileChannelRepository implements ChannelRepository, Serializable {

    private static final FileChannelRepository instance = new FileChannelRepository();
    private final ArrayList<Channel> data;

    public FileChannelRepository() {
        this.data = loadChannels();
    }
    public ArrayList<Channel> getChannels() {
        return data;
    }

    public static FileChannelRepository getInstance() {
        return instance;
    }

    @Override
    public void saveChannels(){
        System.out.println("채널 리스트 저장");
        String filePath = "./data/channels.ser";

        // ArrayList<Channel> 직렬화및 저장
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(data);
            System.out.println("Channel 리스트가 직렬화되어 '" + filePath + "' 파일에 저장되었습니다.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<Channel> loadChannels(){
        ArrayList<Channel> deserializedChannels = null;
        System.out.println("채널 리스트 불러오기");
        String filePath = "./data/channels.ser"; // 유저 직렬화

        if (!new File(filePath).exists() || new File(filePath).length() == 0) {
            return new ArrayList<Channel>(); // 해당 파일이 없으면 빈 ArrayList를 반환
        }

        // ArrayList<Channel> 역직렬화및 반환
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            deserializedChannels = (ArrayList<Channel>) ois.readObject();

            /*
            System.out.println("역직렬화된 Channel 리스트 정보:");

            for (Channel channel : deserializedChannels) {
                System.out.println("------------");
                System.out.println("Channel: " + channel.toString());
            }*/

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return deserializedChannels;
    }
}
