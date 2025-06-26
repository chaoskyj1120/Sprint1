package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.auth_service_dto.LoginRequestDto;
import com.sprint.mission.discodeit.dto.user_service_dto.UserResponseDto;
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
    private final BinaryContentsRepository binaryContentsRepository;

    @Override
    public UserResponseDto logInUser(LoginRequestDto loginRequest) {
        Optional<User> targetUser = userRepository.findUserByUserName(loginRequest.getUserName());

        if (targetUser.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 아이디입니다.");
        }

        User user = targetUser.get();

        if (!user.getPassword().equals(loginRequest.getPassword())) {
            throw new IllegalArgumentException("일치하지 않은 비밀번호 입니다.");
        }

        BinaryContents currentUserPicture = binaryContentsRepository.getBinaryContentsByBinaryContentsId(user.getProfileId());

        // DTO 반환
        return new UserResponseDto(
                user.getId(),
                user.getUserName(),
                user.getEmail(),
                currentUserPicture != null ? currentUserPicture.getBinaryData() : null
        );
    }
}
