package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserCreateDTO;
import com.sprint.mission.discodeit.dto.UserLoggedDataDTO;
import com.sprint.mission.discodeit.dto.UserUpdateDTO;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.BinaryContentsRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor // NOTE 생성자 lombok에서 생성 final이 붙은 필드를 매개변수로 받아 생성자에서 주입
@Service
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final BinaryContentsRepository binaryContentsRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public User createUser(UserCreateDTO userCreateDto) {
        List<User> usersFromFile = userRepository.loadUsers();

        String idForLogin = userCreateDto.getIdForLogin();
        String passwordForLogin = userCreateDto.getPassword();
        String userName = userCreateDto.getName();
        String email = userCreateDto.getEmail();
        String profilePicturePath = userCreateDto.getProfilePicturePath();

        // 아이디 중복 검사
        boolean isIdDuplicated = usersFromFile.stream()
                .anyMatch(user -> user.getIdForLogin().equals(idForLogin));
        if (isIdDuplicated) {
            System.out.println("이미 존재하는 아이디입니다.");
            return null;
        }

        // 이름 중복 검사
        boolean isNameDuplicated = usersFromFile.stream()
                .anyMatch(user -> user.getUserName().equals(userName));
        if (isNameDuplicated) {
            System.out.println("이미 존재하는 이름입니다.");
            return null;
        }

        // 이메일 중복 검사
        boolean isEmailDuplicated = usersFromFile.stream()
                .anyMatch(user -> user.getEmail().equals(email));
        if (isEmailDuplicated) {
            System.out.println("이미 존재하는 이메일입니다.");
            return null;
        }

        // 프로필 이미지 읽기
        byte[] profileImageBytes = null;
        if (profilePicturePath != null && !profilePicturePath.isEmpty()) {
            File imageFile = new File(profilePicturePath);
            try (FileInputStream fis = new FileInputStream(imageFile)) {
                profileImageBytes = fis.readAllBytes();
            } catch (IOException e) {
                System.out.println("프로필 이미지를 읽는 데 실패했습니다: " + e.getMessage());
                return null;
            }
        }

        BinaryContents profileImg = new BinaryContents(profilePicturePath, BinaryContentType.USER_PROFILE_IMAGE, profileImageBytes);
        // 사용자 생성
        User user = new User(idForLogin, passwordForLogin, userName, email, profileImg.getId());
        userRepository.createUser(user);

        profileImg.setReferenceId(user.getId());
        binaryContentsRepository.createBinaryContents(profileImg);

        UserStatus userStatus = new UserStatus(user, false); // 새로 생성한 것을 저장 해야 하는데
        userStatusRepository.createUserStatus(userStatus);

        return userRepository.getUserById(user.getId());
    }

    @Override
    public void updateUser(UserUpdateDTO userUpdateDTO) {
        List<User> users = userRepository.loadUsers();

        User targetUser = users.stream()
                .filter(userFromFile -> userFromFile.equalsId(userUpdateDTO.getUserId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 유저 없음"));

        targetUser.setIdForLogin(userUpdateDTO.getNewUserIdForLogin());
        targetUser.setUserName(userUpdateDTO.getNewName());
        targetUser.setPasswordForLogin(userUpdateDTO.getNewPassword());;
        targetUser.setEmail(userUpdateDTO.getNewEmail());

        // 유저의 프로필아이디를 가진 사진의 패스가 같아야함


        boolean isSameProfile = binaryContentsRepository.loadBinaryContents().stream()
                .anyMatch(binaryContent -> binaryContent.equalsId(targetUser.getProfileId())
                        && binaryContent.getBinaryContentsPath().equals(userUpdateDTO.getNewProfileImagePath()));

        if (!isSameProfile) {
            // 프로필 이미지 읽기
            byte[] profileImageBytes = null;
            if (userUpdateDTO.getNewProfileImagePath() != null && !userUpdateDTO.getNewProfileImagePath().isEmpty()) {
                File imageFile = new File(userUpdateDTO.getNewProfileImagePath());
                try (FileInputStream fis = new FileInputStream(imageFile)) {
                    profileImageBytes = fis.readAllBytes();
                } catch (IOException e) {
                    System.out.println("프로필 이미지를 읽는 데 실패했습니다: " + e.getMessage());
                    userRepository.saveUsers(users);
                    return;
                }
            }
            BinaryContents profileImg = new BinaryContents(userUpdateDTO.getNewProfileImagePath(), BinaryContentType.USER_PROFILE_IMAGE, profileImageBytes);
            profileImg.setReferenceId(targetUser.getId());
            binaryContentsRepository.delete(targetUser.getProfileId());
            targetUser.setProfileId(profileImg.getId());
            binaryContentsRepository.createBinaryContents(profileImg);
        }
        
        userRepository.saveUsers(users);
    }

    @Override
    public void deleteUser(User user) {

        List<Channel> channelsFromFile = channelRepository.loadChannels();

        boolean hasHostChannel = channelsFromFile.stream()
                .anyMatch(channel -> channel.getHostUserId().equals(user.getId()));

        if (hasHostChannel) {
            // 채널 주인이란 의미이므로 return
            System.out.println("호스트인 채널이 있어서 유저를 삭제할 수 없습니다.");
            return;
        }

        User userFromFile = userRepository.getUserById(user.getId());

        if (userFromFile == null) {
            System.out.println("해당 유저가 존재하지 않습니다.");
            return;
        }

        for (Channel channelFromFile : channelsFromFile) {
            if(userFromFile.getChannelIds().contains(channelFromFile.getId())){
                channelFromFile.removeUser(user);
            }
        }

        /*
        [ ] 관련된 도메인도 같이 삭제합니다.
        BinaryContent(프로필), UserStatus
        */
        binaryContentsRepository.delete(userFromFile.getProfileId());
        userStatusRepository.delete(userFromFile.getId());

        channelRepository.saveChannels(channelsFromFile);
        userRepository.deleteUser(user);
    }

    @Override
    public void restoreUser(User user) {
        userRepository.restoreUser(user);
    }

    @Override
    public void printActiveUsers(){
        List<User> data = userRepository.loadUsers();

        System.out.printf("전체 유저 조회(탈퇴 유저 미포함), 총 유저 수: %d \n", (data.stream()
                .filter(user -> user.getStatus().equals(UserActivationState.ACTIVE))).toList().size());
        data.stream()
                .filter(user -> user.getStatus().equals(UserActivationState.ACTIVE))
                .forEach(user -> System.out.printf("유저 이름: '%s', 유저 ID: '%s'\n", user.getUserName(), user.getId()));
    }

    @Override
    public void printUser(User user) {
        System.out.print("단일 유저 조회\n");
        System.out.printf("유저 이름: '%s', 유저 ID: '%s'\n", user.getUserName(), user.getId());
    }

    @Override
    public void printAllUsers() {
        List<User> data = userRepository.loadUsers();
        System.out.printf("전체 유저 조회(탈퇴 유저 포함), 유저 수: %d \n", data.size());
        data
                .forEach(user -> System.out.printf("유저 이름: '%s', 유저 ID: '%s'\n", user.getUserName(), user.getId()));
    }

    @Override
    public void printDeactivatedUsers() {
        List<User> data = userRepository.loadUsers();
        System.out.printf("탈퇴 유저 조회, 총 유저 수: %d \n", (data.stream()
                .filter(user -> user.getStatus().equals(UserActivationState.DEACTIVE))).toList().size());
        data.stream()
                .filter(user -> user.getStatus().equals(UserActivationState.DEACTIVE)) // getStatus는 boolean 값이므로 equal 생략
                .forEach(user -> System.out.printf("유저 이름: '%s', 유저 ID: '%s'\n", user.getUserName(), user.getId()));
    }


    @Override
    public UserLoggedDataDTO findUserById(String userIdForLogin){
        return findAll().stream().filter(u -> u.getUserId().equals(userIdForLogin)).findFirst().orElse(null);
    }

    @Override
    public void logOutUser(User user){
        UserStatus userStatus = new UserStatus(user, false); // 새로 생성한 것을 저장 해야 하는데
        userStatusRepository.createUserStatus(userStatus);
    }

    @Override
    public UserStatus isOnline(User user){

        UserStatus lastStatus = userStatusRepository.loadUserStatuses().stream()
                .filter(userStatus -> userStatus.getUserId().equals(user.getId()))
                .reduce((first, second) -> second)
                .orElse(null); // 또는 예외 처리

        assert lastStatus != null;
        return lastStatus;

        // true는 접속 시간
        // faisl는
    }

    @Override
    public List<UserLoggedDataDTO> findAll(){
        /*
        UserStatus
        사용자 별 마지막으로 확인된 접속 시간을 표현하는 도메인 모델입니다. 사용자의 온라인 상태를 확인하기 위해 활용합니다.
        마지막 접속 시간을 기준으로 현재 로그인한 유저로 판단할 수 있는 메소드를 정의하세요.
        마지막 접속 시간이 현재 시간으로부터 5분 이내이면 현재 접속 중인 유저로 간주합니다.*/

        List<UserLoggedDataDTO> userStatuses = new ArrayList<>();
        List<User> usersFromFile = userRepository.loadUsers();

        for (User user : usersFromFile) {
            UserStatus userStatusFromCurrentUser = isOnline(user);
            userStatuses.add(new UserLoggedDataDTO(user.getId(), user.getIdForLogin(), user.getUserName(), userStatusFromCurrentUser.getLoggedIn()));
        }

        return userStatuses;
    }
}
