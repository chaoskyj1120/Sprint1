package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user_status_dto.CreateUserStatusRequestDto;
import com.sprint.mission.discodeit.dto.user_status_dto.UpdateUserStatusRequestDto;
import com.sprint.mission.discodeit.dto.user_status_dto.UserStatusResponseDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;


    @Override
    public UserStatusResponseDto createUserStatus(CreateUserStatusRequestDto createUserStatusRequestDto){

        //create
        //[ ] DTO를 활용해 파라미터를 그룹화합니다.
        //[ ] 관련된 User가 존재하지 않으면 예외를 발생시킵니다.
        //[ ] 같은 User와 관련된 객체가 이미 존재하면 예외를 발생시킵니다.

        Optional<User> userFromFile = userRepository.findUserById(createUserStatusRequestDto.getUserId());
        optionalUserIsEmpty(userFromFile);
        User user = userFromFile.get();

        Optional<UserStatus> userStatus = userStatusRepository.findUserStatusByUserId(user.getId());

        if (userStatus.isPresent()) {
            //throw new RuntimeException("이미 존재하는 useStatus 입니다.");
            UserStatus newUserStatus = userStatus.get();
            return new UserStatusResponseDto(newUserStatus.getId(), user.getId(), user.getUserName(), newUserStatus.getLoggedIn(), newUserStatus.getUpdatedAt());
        }


        UserStatus newUserStatus = new UserStatus(user); // 회원 가입할 때는 false로 생성
        userStatusRepository.createUserStatus(newUserStatus);

        return new UserStatusResponseDto(newUserStatus.getId(), user.getId(), user.getUserName(), newUserStatus.getLoggedIn(), newUserStatus.getUpdatedAt());
    }

    @Override
    public UserStatusResponseDto updateUserStatus(UpdateUserStatusRequestDto updateUserStatusRequestDto){
        Optional<User> userFromFile  = userRepository.findUserById(updateUserStatusRequestDto.getUserId());
        optionalUserIsEmpty(userFromFile);
        User user = userFromFile.get();

        Optional<UserStatus> optionalUserStatus = userStatusRepository.findUserStatusByUserId(updateUserStatusRequestDto.getUserId());
        optionalUserStatusIsEmpty(optionalUserStatus);

        UserStatus userStatus = optionalUserStatus.get();

        // ✅ 잘못된 비교 수정: ID가 같지 않으면 예외를 던져야 하는 것이 맞습니다.
        if (!userStatus.getUserId().equals(updateUserStatusRequestDto.getUserId())) {
            throw new RuntimeException("userId가 userStatus의 userId 와 다릅니다.");
        }

        userStatus.updateUpdatedAt();
        userStatusRepository.updateUserStatus(userStatus);

        // 최신 상태 다시 가져오기
        userStatus = userStatusRepository.findUserStatusByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("업데이트 후 userStatus 를 찾을 수 없습니다."));

        return new UserStatusResponseDto(
                userStatus.getId(),
                user.getId(),
                user.getUserName(),
                userStatus.getLoggedIn(),
                userStatus.getUpdatedAt()
        );
    }


    @Override
    public List<UserStatusResponseDto> findAllUserStatus(){

        List<UserStatusResponseDto> userStatusResponse = new ArrayList<>();
        List<UserStatus> userStatuses = userStatusRepository.loadUserStatuses();

        for (UserStatus userStatus : userStatuses) {
            Optional<User> userFromFile = userRepository.findUserById(userStatus.getUserId());
            optionalUserIsEmpty(userFromFile);
            User user = userFromFile.get();

            UserStatusResponseDto userStatusResponseDto = new UserStatusResponseDto(userStatus.getId(), userStatus.getUserId(), user.getUserName(), userStatus.getLoggedIn(), userStatus.getUpdatedAt());
            userStatusResponse.add(userStatusResponseDto);
        }

        return userStatusResponse;
    }

    @Override
    public UserStatusResponseDto findUserStatusByUserId(UUID userId){
        Optional<UserStatus> optionalUserStatus = userStatusRepository.findUserStatusByUserStatusId(userId);
        UserStatus userStatus = optionalUserStatus.get();
        Optional<User> userFromFile = userRepository.findUserById(userId);
        optionalUserIsEmpty(userFromFile);
        User user = userFromFile.get();

        return new UserStatusResponseDto(userStatus.getId(), user.getId(), user.getUserName(), userStatus.getLoggedIn(), userStatus.getUpdatedAt());
    }


    @Override
    public void deleteUserStatusByUserId(UUID userId){
        userStatusRepository.deleteUserStatusByUserId(userId);
    }

    @Override
    public void deleteUserStatusByUserStatusId(UUID userStatusId){
        userStatusRepository.deleteUserStatusByUserStatusId(userStatusId);
    }


    private void optionalUserIsEmpty(Optional<User> user) {
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User 정보가 없습니다.");
        }
    }

    private void optionalChannelIsEmpty(Optional<Channel> channel) {
        if (channel.isEmpty()) {
            throw new IllegalArgumentException("Channel 정보가 없습니다.");
        }
    }

    private void optionalUserStatusIsEmpty(Optional<UserStatus> userStatus) {
        if (userStatus.isEmpty()) {
            throw new IllegalArgumentException("UserStatus 정보가 없습니다.");
        }
    }
}
