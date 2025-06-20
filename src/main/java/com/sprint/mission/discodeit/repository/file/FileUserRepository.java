package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserActivationState;
import com.sprint.mission.discodeit.repository.ChannelRepository;
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
public class FileUserRepository implements UserRepository, Serializable {

    private static final String USER_FILE_PATH = "./data/user.ser";

    @Override
    public List<User> loadUsers() {
        File file = new File(USER_FILE_PATH);

        if (!file.exists() || file.length() == 0) {
            return new ArrayList<User>(); // 해당 파일이 없거나 비어 있다면 빈 ArrayList를 반환
        }
        // ArrayList<User> 역직렬화및 반환
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USER_FILE_PATH))) {
            return (List<User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public void saveUsers(List<User> users) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USER_FILE_PATH))) {
            oos.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createUser(User user) {
        List<User> users = loadUsers();
        users.add(user);
        saveUsers(users); // 유저 생성은 유저한테만 영향을 미치니까 채널과 메시지는 저장하지 않음
    }

    @Override
    public void updateUser(User user, String newName) {
        List<User> users = loadUsers();
        user.setUserName(newName);

        for (User userFromFile : users) {
            if (userFromFile.equalsId(user)) {
                userFromFile.setUserName(newName);
                break;
            }
        }
        saveUsers(users);
    }

    @Override
    public void deleteUser(User user) {
        List<User> usersFromFile = loadUsers();

        for (User userFromFile : usersFromFile) {
            if (userFromFile.equalsId(user)) {
                userFromFile.setStatus(UserActivationState.DEACTIVE);
                userFromFile.clearChannelIds();
                break; // 유저 찾았으니 루프 종료
            }
        }

        saveUsers(usersFromFile);
    }

    @Override
    public void restoreUser(User user) {
        List<User> users = loadUsers();
        for (User targetUserInFile : users) {
            if (targetUserInFile.equalsId(user)){
                targetUserInFile.setStatus(UserActivationState.ACTIVE);
                break;
            }
        }
        saveUsers(users);
    }

    @Override
    public User getUserById(UUID userId) {
        return loadUsers().stream()
                .filter(u -> u.equalsId(userId))
                .findFirst()
                .orElse(null);
    }
}