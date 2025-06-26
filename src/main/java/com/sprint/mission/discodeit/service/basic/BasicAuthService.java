package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.auth_service_dto.LoginRequestDto;
import com.sprint.mission.discodeit.dto.user_service_dto.UserResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContents;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentsRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
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
        optionalUserIsEmpty(targetUser);
        User user = targetUser.get();

        if (!user.getPassword().equals(loginRequest.getPassword())) {
            throw new IllegalArgumentException("일치하지 않은 비밀번호 입니다.");
        }

        Optional<BinaryContents> currentUserPicture = binaryContentsRepository.findBinaryContentsByBinaryContentsId(user.getProfileId());
        optionalBinaryContentsIsEmpty(currentUserPicture);
        BinaryContents binaryContents = currentUserPicture.get();
        // DTO 반환
        return new UserResponseDto(
                user.getId(),
                user.getUserName(),
                user.getEmail(),
                currentUserPicture != null ? binaryContents.getBinaryData() : null
        );
    }

    private void optionalUserIsEmpty(Optional<User> user) {
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User 정보가 없습니다.");
        }
    }

    private void optionalBinaryContentsIsEmpty(Optional<BinaryContents> binaryContents) {
        if (binaryContents.isEmpty()) {
            throw new IllegalArgumentException("ReadStatus 정보가 없습니다.");
        }
    }
}
