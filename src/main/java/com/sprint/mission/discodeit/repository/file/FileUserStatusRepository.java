package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Repository
public class FileUserStatusRepository implements UserStatusRepository, Serializable {

    private static final String USER_STATUS_FILE_PATH = "./data/userStatus.ser";

    @Override
    public List<UserStatus> loadUserStatuses() {
        File file = new File(USER_STATUS_FILE_PATH);

        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>(); // 비어 있으면 빈 리스트 반환
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<UserStatus>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public void saveStatuses(List<UserStatus> userStatuses) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USER_STATUS_FILE_PATH))) {
            oos.writeObject(userStatuses);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createUserStatus(UserStatus userStatus) {
        List<UserStatus> userStatuses = loadUserStatuses();
        userStatuses.add(userStatus);
        saveStatuses(userStatuses);
    }

    @Override
    public void delete(UUID userId){
        List<UserStatus> userStatuses = loadUserStatuses();
        userStatuses.removeIf(userStatus -> userStatus.getUserId().equals(userId));
        saveStatuses(userStatuses);
    }
}
