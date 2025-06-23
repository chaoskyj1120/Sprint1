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
import java.util.List;

@RequiredArgsConstructor // NOTE 생성자 lombok에서 생성 final이 붙은 필드를 매개변수로 받아 생성자에서 주입
@Service
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final BinaryContentsRepository binaryContentsRepository;
    private final UserStatusRepository userStatusRepository;
    private final AuthService authService;

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
    public List<UserLoggedDataDTO> findAll(){
        return authService.getLoggedUserStatuses();
    }

    @Override
    public UserLoggedDataDTO findUserById(String userIdForLogin){
        return findAll().stream().filter(u -> u.getUserId().equals(userIdForLogin)).findFirst().orElse(null);
    }
}
