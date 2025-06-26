package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.auth_service_dto.LoginRequestDTO;
import com.sprint.mission.discodeit.dto.user_service_dto.UserDTO;
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
    public UserDTO logInUser(LoginRequestDTO loginRequest) {
        Optional<User> targetUser = userRepository.findUserByUserName(loginRequest.getUserName());

        if (targetUser.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 아이디입니다.");
        }

        User user = targetUser.get();

        if (!user.getPassword().equals(loginRequest.getPassword())) {
            throw new IllegalArgumentException("일치하지 않은 비밀번호 입니다.");
        }

        // 로그인 성공 → UserStatus 갱신 (로그인 상태 저장)
        UserStatus userStatus = new UserStatus(user, true);
        userStatusRepository.createUserStatus(userStatus); // NOTE: 기존 상태를 대체하거나 갱신하는 방식이라면 update 방식도 고려

        // 프로필 이미지 조회
        BinaryContents currentUserPicture = binaryContentsRepository.getBinaryContentsByBinaryContentsId(user.getProfileId());

        // DTO 반환
        return new UserDTO(
                user.getId(),
                user.getUserName(),
                user.getEmail(),
                currentUserPicture != null ? currentUserPicture.getBinaryData() : null
        );
    }
}
