package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.LoginRequestDTO;
import com.sprint.mission.discodeit.dto.UserLoggedDataDTO;
import com.sprint.mission.discodeit.dto.UserLoginDataDTO;
import com.sprint.mission.discodeit.entity.BinaryContents;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentsRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentsRepository binaryContentsRepository;

    @Override
    public UserLoginDataDTO logInUser(LoginRequestDTO loginRequest) {
        List<User> users = userRepository.loadUsers();

        // 아이디 일치하는 유저 찾기
        User targetUser = users.stream()
                .filter(user -> user.getIdForLogin().equals(loginRequest.getIdForLogin()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));

        // 비밀번호 검증
        if (!targetUser.getPasswordForLogin().equals(loginRequest.getPasswordForLogin())) {
            throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
        }

        // 로그인 성공 → UserStatus 갱신 (로그인 상태 저장)
        UserStatus userStatus = new UserStatus(targetUser, true);
        userStatusRepository.createUserStatus(userStatus); // NOTE: 기존 상태를 대체하거나 갱신하는 방식이라면 update 방식도 고려

        // 프로필 이미지 조회
        BinaryContents currentUserPicture = binaryContentsRepository.getBinaryContentsById(targetUser.getProfileId());

        // DTO 반환
        return new UserLoginDataDTO(
                targetUser.getId(),
                targetUser.getIdForLogin(),
                targetUser.getUserName(),
                targetUser.getEmail(),
                currentUserPicture != null ? currentUserPicture.getBinaryData() : null
        );
    }
}
