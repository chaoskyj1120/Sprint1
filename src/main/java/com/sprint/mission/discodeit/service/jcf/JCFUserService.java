package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {


    private static JCFUserService instance = new JCFUserService();
    private final ArrayList<User> usersData;

    private JCFUserService() {
        usersData = new ArrayList<>();
    }

    public static JCFUserService getInstance() {
        return instance;
    }

    @Override
    public void addUser(User user) {
        System.out.printf("유저 추가 \n유저 이름: '%s', 유저 ID: '%s'\n", user.getUserName() ,user.getId());
        usersData.add(user);
    }

    @Override
    public void updateUser(User user, String newName) {
        System.out.println("유저의 이름을 변경합니다.");
        System.out.printf("변경 전: '%s', 변경 후: '%s' \n", user.getUserName(), newName);
        user.reUserName(newName);
    }

    @Override
    public void deleteUser(User user) {
        System.out.printf("유저 삭제 \n유저 이름: '%s', 유저 ID: '%s'\n" ,user.getUserName(), user.getId());
        usersData.remove(user);
    }

    @Override
    public void printAllUsers() {
        System.out.printf("전체 유저 조회, 유저 수: %d \n", usersData.size());
        usersData.forEach(user -> System.out.printf("유저 이름: '%s', 유저 ID: '%s'\n", user.getUserName(), user.getId()));
    }

    @Override
    public void printUser(User user) {
        System.out.print("단일 유저 조회\n");
        System.out.printf("유저 이름: '%s', 유저 ID: '%s'\n", user.getUserName(), user.getId());
    }
}
