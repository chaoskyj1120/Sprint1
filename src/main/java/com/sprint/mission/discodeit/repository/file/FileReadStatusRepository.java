package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.UserActivationState;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Repository
public class FileReadStatusRepository implements ReadStatusRepository, Serializable {

    private static final String READ_STATUS_FILE_PATH = "./data/readStatus.ser";

    @Override
    public List<ReadStatus> loadReadStatuses() {
        File file = new File(READ_STATUS_FILE_PATH);

        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>(); // 비어 있으면 빈 리스트 반환
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<ReadStatus>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public void saveReadStatuses(List<ReadStatus> readStatuses) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(READ_STATUS_FILE_PATH))) {
            oos.writeObject(readStatuses);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createReadStatus(ReadStatus readStatus) {
        List<ReadStatus> readStatuses = loadReadStatuses();
        readStatuses.add(readStatus);
        saveReadStatuses(readStatuses);
    }

    @Override
    public List<ReadStatus> findReadStatusesByUserId(UUID userId){
        return loadReadStatuses().stream()
                .filter(readStatus -> readStatus.getUserId().equals(userId)).toList();
    }

    @Override
    public List<ReadStatus> findReadStatusesByChannelId(UUID channelId){
        return loadReadStatuses().stream()
                .filter(readStatus -> readStatus.getChannelId().equals(channelId)).toList();
    }

    @Override
    public void deleteReadStatus(UUID userId, UUID channelId){
        loadReadStatuses().removeIf(readStatus -> readStatus.getUserId().equals(userId) && readStatus.getChannelId().equals(channelId));
        saveReadStatuses(loadReadStatuses());
    }

    @Override
    public ReadStatus findReadStatusesByUserIdAndChannelId(UUID userId, UUID channelId){
        return loadReadStatuses().stream()
                .filter(readStatus -> readStatus.getUserId().equals(userId)
                                && readStatus.getChannelId().equals(channelId)).findFirst().orElse(null);
    }

    @Override
    public ReadStatus findReadStatusesByreadStatusId(UUID readStatusId){
        return loadReadStatuses().stream()
                .filter(readStatus -> readStatus.getId().equals(readStatusId))
                .findFirst().orElse(null);

    }

    @Override
    public void updateReadStatus(ReadStatus readStatus){
        List<ReadStatus> readStatusesFromFile = loadReadStatuses();
        for (int i = 0; i < readStatusesFromFile.size(); i++) {
            if (readStatusesFromFile.get(i).equalsId(readStatus)) {
                readStatusesFromFile.set(i, readStatus); // 리스트 내부 요소를 직접 교체
                saveReadStatuses(readStatusesFromFile);
                return;
            }
        }
    }

    @Override
    public void deleteReadStatusByChannelId(UUID channelId){
        List<ReadStatus> readStatusesFromFile = loadReadStatuses();
        readStatusesFromFile.removeIf(readStatus -> readStatus.getChannelId().equals(channelId));
        saveReadStatuses(readStatusesFromFile);
    }

    @Override
    public void deleteReadStatusByReadStatusId(UUID readStatusId){
        List<ReadStatus> readStatusesFromFile = loadReadStatuses();
        readStatusesFromFile.removeIf(readStatus -> readStatus.equalsId(readStatusId));
        saveReadStatuses(readStatusesFromFile);
    }
}
