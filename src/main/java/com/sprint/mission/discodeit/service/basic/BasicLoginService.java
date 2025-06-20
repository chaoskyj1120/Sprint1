package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class BasicLoginService implements LoginService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public boolean authenticateUser(String idForLogin, String passwordForLogin) {
        List<User> users = userRepository.loadUsers();

        Optional<User> loginUser = users.stream()
                .filter(user -> user.getIdForLogin().equals(idForLogin))
                .findFirst();

        if (!loginUser.isPresent()) {
            System.out.println("존재하지 않는 아이디입니다.");
            return false;
        }

        User user = loginUser.get();
        if (!user.getPasswordForLogin().equals(passwordForLogin)) {
            System.out.println("비밀번호가 틀렸습니다.");
            return false;
        }

        return true;
    }

    @Override
    public User logInUser(String idForLogin){
        List<User> users = userRepository.loadUsers();
        User targetUser = users.stream().filter(user -> user.getIdForLogin().equals(idForLogin)).findFirst().get();
        // 여기서 userStatus를 갱신
        // UserStatus를 로그인 할 때 마다 새로 생성
        // 
        UserStatus userStatus = new UserStatus(targetUser, true); // 새로 생성한 것을 저장 해야 하는데
        userStatusRepository.createUserStatus(userStatus);

        // 로그아웃한 시점에 userStatus이거를 들고있어야 로그아웃 할 수 있음 -> 불가능?

        return targetUser;
    }

    @Override
    public void logOutUser(User user){
        UserStatus userStatus = new UserStatus(user, false); // 새로 생성한 것을 저장 해야 하는데
        userStatusRepository.createUserStatus(userStatus);
    }

    @Override
    public boolean isOnline(User user){

        UserStatus lastStatus = userStatusRepository.loadUserStatuses().stream()
                .filter(userStatus -> userStatus.getUserId().equals(user.getId()))
                .reduce((first, second) -> second)
                .orElse(null); // 또는 예외 처리

        assert lastStatus != null;
        return lastStatus.getLoggedIn();
    }
}
