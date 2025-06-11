package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.Optional;

public class BasicUserService implements UserService {
    private static final BasicUserService instance = new BasicUserService();
    private final FileUserRepository fileUserRepository;
    private final ArrayList<User> data;

    public BasicUserService() {
        fileUserRepository = FileUserRepository.getInstance();
        data = fileUserRepository.getUsers();
    }

    public static BasicUserService getInstance() {
        return instance;
    }

    @Override
    public User createUser(String userName) {
        return fileUserRepository.createUser(userName);
    }

    @Override
    public void updateUser(User user, String newName) {
        fileUserRepository.updateUser(user, newName);
    }

    @Override
    public void deleteUser(User user) {
        fileUserRepository.deleteUser(user);
    }

    @Override
    public void restoreUser(User user) {
        fileUserRepository.restoreUser(user);
    }

    @Override
    public void printActiveUsers(){
        // status를 판별하는 식을 추가
        System.out.printf("전체 유저 조회(탈퇴 유저 미포함), 총 유저 수: %d \n", (data.stream()
                .filter(user -> user.getStatus().equals(UserStatus.ACTIVE))).toList().size());
        data.stream()
                .filter(user -> user.getStatus().equals(UserStatus.ACTIVE))
                .forEach(user -> System.out.printf("유저 이름: '%s', 유저 ID: '%s'\n", user.getUserName(), user.getId()));
    }

    @Override
    public void printUser(User user) {
        System.out.print("단일 유저 조회\n");
        System.out.printf("유저 이름: '%s', 유저 ID: '%s'\n", user.getUserName(), user.getId());
    }

    @Override
    public void printAllUsers() {
        System.out.printf("전체 유저 조회(탈퇴 유저 포함), 유저 수: %d \n", data.size());
        data
                .forEach(user -> System.out.printf("유저 이름: '%s', 유저 ID: '%s'\n", user.getUserName(), user.getId()));
    }

    @Override
    public void printDeactivatedUsers() {
        System.out.printf("탈퇴 유저 조회, 총 유저 수: %d \n", (data.stream()
                .filter(user -> user.getStatus().equals(UserStatus.DEACTIVE))).toList().size());
        data.stream()
                .filter(user -> user.getStatus().equals(UserStatus.DEACTIVE)) // getStatus는 boolean 값이므로 equal 생략
                .forEach(user -> System.out.printf("유저 이름: '%s', 유저 ID: '%s'\n", user.getUserName(), user.getId()));
    }
}
