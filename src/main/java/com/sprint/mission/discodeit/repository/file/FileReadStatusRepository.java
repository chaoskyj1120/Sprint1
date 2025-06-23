package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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
}
