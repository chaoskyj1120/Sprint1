package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user_service_dto.*;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.BinaryContentsRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor // NOTE 생성자 lombok에서 생성 final이 붙은 필드를 매개변수로 받아 생성자에서 주입
@Service
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final BinaryContentsRepository binaryContentsRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public UserDTO createUser(UserCreateRequestDTO userCreateRequestDTO) {
        final String DEFAULT_PROFILE_IMG_PATH = "./src/main/resources/static/basicUserProfileImage.png";

        String userName = userCreateRequestDTO.getUsername();
        String password = userCreateRequestDTO.getPassword();
        String userEmail = userCreateRequestDTO.getEmail();
        String profilePicturePath = Optional.ofNullable(userCreateRequestDTO.getProfileImagePath())
                .orElse(DEFAULT_PROFILE_IMG_PATH);

        validateUserNameNotDuplicated(userName);
        validateUserEmailNotDuplicated(userEmail);

        byte[] profileImageBytes;
        try (FileInputStream fis = new FileInputStream(profilePicturePath)) {
            profileImageBytes = fis.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("프로필 이미지를 읽는 데 실패했습니다: " + e.getMessage(), e);
        }

        BinaryContents profileImg = new BinaryContents(profilePicturePath, BinaryContentType.USER_PROFILE_IMAGE, profileImageBytes);

        User user = new User(userName, password, userEmail, profileImg.getId());
        userRepository.createUser(user);

        profileImg.setReferenceId(user.getId());
        binaryContentsRepository.createBinaryContents(profileImg);

        UserStatus userStatus = new UserStatus(user, false); // 로그인 상태는 false
        userStatusRepository.createUserStatus(userStatus);

        return new UserDTO(
                user.getId(),
                user.getUserName(),
                user.getEmail(),
                profileImageBytes // 프로필 이미지 바이트 직접 반환
        );
    }


    @Override
    public void updateUser(UserUpdateRequestDTO userUpdateRequestDTO) {

        User targetUser = userRepository.findUserById(userUpdateRequestDTO.getUserId());

        isExistUserByUserId(userUpdateRequestDTO.getUserId());

        targetUser.setUserName(userUpdateRequestDTO.getNewUserName());
        targetUser.setEmail(userUpdateRequestDTO.getNewEmail());

        // 유저의 프로필아이디를 가진 사진의 패스가 같아야함

        if (!compareProfile(userUpdateRequestDTO)) {
            // 프로필 이미지 읽기
            byte[] profileImageBytes = null;
            if (userUpdateRequestDTO.getNewProfileImagePath() != null && !userUpdateRequestDTO.getNewProfileImagePath().isEmpty()) {
                File imageFile = new File(userUpdateRequestDTO.getNewProfileImagePath());
                try (FileInputStream fis = new FileInputStream(imageFile)) {
                    profileImageBytes = fis.readAllBytes();
                } catch (IOException e) {
                    System.out.println("프로필 이미지를 읽는 데 실패했습니다: " + e.getMessage());
                    userRepository.updateUser(targetUser);
                    return;
                }
            }
            BinaryContents profileImg = new BinaryContents(userUpdateRequestDTO.getNewProfileImagePath(), BinaryContentType.USER_PROFILE_IMAGE, profileImageBytes);
            profileImg.setReferenceId(targetUser.getId());
            binaryContentsRepository.delete(targetUser.getProfileId());
            targetUser.setProfileId(profileImg.getId());
            binaryContentsRepository.createBinaryContents(profileImg);
        }
        
        userRepository.updateUser(targetUser);
    }

    @Override
    public void deleteUser(UserDTO userDTO) {

        Optional<User> userFromFile = userRepository.findUserByUserId(userDTO.getUserId());

        if (userFromFile.isEmpty()){
            System.out.println("해당 유저가 존재하지 않습니다.");
            return;
        }
        User user = userFromFile.get();
        /*
        [ ] 관련된 도메인도 같이 삭제합니다.
        BinaryContent(프로필), UserStatus
        */
        binaryContentsRepository.delete(user.getProfileId());
        userStatusRepository.delete(user.getId());

        channelRepository.deleteUserFromChannels(user);
        userRepository.deleteUser(user);
    }

    @Override
    public void restoreUser(String userName) {
        isExistUserByUserName(userName);
        userRepository.restoreUser(userName);
    }

    @Override
    public UserRecentConnectionDTO findUserConnectionByUserName(UserConnectionRequestDTO userConnectionRequestDTO){
        //User user로 받으면 좋을것같다? -> 컨트롤러가 User를 들고 있을 수 없으니 할 수 없음, userName하나만을 가지고 있는 DTO를 사용
        isExistUserByUserName(userConnectionRequestDTO.getUserName());
        return findAllConnection().stream().filter(u -> u.getUserName().equals(userConnectionRequestDTO.getUserName())).findFirst().orElse(null);
    }

    @Override
    public void logOutUser(UserDTO userDTO){
        isExistUserByUserName(userDTO.getUserName());
        User user = userRepository.findUserById(userDTO.getUserId());
        UserStatus userStatus = new UserStatus(user, false); // 새로 생성한 것을 저장 해야 하는데
        userStatusRepository.createUserStatus(userStatus);
    }

    @Override
    public UserStatus isOnline(User user){
        // 검증로직은 따로 필요없다.
        return userStatusRepository.getLastUserStatus(user);
    }

    @Override
    public List<UserRecentConnectionDTO> findAllConnection(){
        /*
        UserStatus
        사용자 별 마지막으로 확인된 접속 시간을 표현하는 도메인 모델입니다. 사용자의 온라인 상태를 확인하기 위해 활용합니다.
        마지막 접속 시간을 기준으로 현재 로그인한 유저로 판단할 수 있는 메소드를 정의하세요.
        마지막 접속 시간이 현재 시간으로부터 5분 이내이면 현재 접속 중인 유저로 간주합니다.*/

        List<UserRecentConnectionDTO> userStatuses = new ArrayList<>();
        List<User> usersFromFile = userRepository.loadUsers();


        for (User user : usersFromFile) {
            UserStatus userStatusFromCurrentUser = isOnline(user);
            userStatuses.add(new UserRecentConnectionDTO(user.getId(), user.getUserName(), userStatusFromCurrentUser.getLoggedIn()));
        }

        return userStatuses;
    }

    @Override
    public List<UserDTO> findAllUserDTO(){
        List<User> usersFromFile = userRepository.loadUsers();
        List<UserDTO> userDTOs = new ArrayList<>();
        for (User user : usersFromFile) {
            userDTOs.add(new UserDTO(user.getId(), user.getUserName(), user.getEmail(), binaryContentsRepository.getBinaryContentsByBinaryContentsId(user.getProfileId()).getBinaryData()));
        }
        return userDTOs;
    }

    @Override
    public List<UserDTO> findAllActiveUserDTO() {
        return userRepository.loadUsers().stream()
                .filter(user -> user.getStatus() == UserActivationState.ACTIVE)
                .map(user -> new UserDTO(
                        user.getId(),
                        user.getUserName(),
                        user.getEmail(),
                        binaryContentsRepository
                                .getBinaryContentsByBinaryContentsId(user.getProfileId())
                                .getBinaryData()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> findAllDeactiveUserDTO() {
        return userRepository.loadUsers().stream()
                .filter(user -> user.getStatus() == UserActivationState.DEACTIVE)
                .map(user -> new UserDTO(
                        user.getId(),
                        user.getUserName(),
                        user.getEmail(),
                        null //삭제된 유저이므로 사진이 없음
                ))
                .collect(Collectors.toList());
    }


    private void validateUserNameNotDuplicated(String userName) {
        if (userRepository.findUserByUserName(userName).isPresent()) {
            throw new IllegalStateException("이미 존재하는 사용자 이름입니다: " + userName);
        }
    }

    private void validateUserEmailNotDuplicated(String userEmail) {
        if (userRepository.findUserByUserName(userEmail).isPresent()) {
            throw new IllegalStateException("이미 존재하는 사용되고 있는 이메일입니다: " + userEmail);
        }
    }

    private void isExistUserByUserName(String userName) {
        userRepository.findUserByUserName(userName)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userName));
    }

    private void isExistUserByUserEmail(String userEmail) {
        userRepository.findUserByUserName(userEmail)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userEmail));
    }

    private void isExistUserByUserId(UUID userId) {
        userRepository.findUserByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));
    }

    private boolean compareProfile(UserUpdateRequestDTO userUpdateRequestDTO){
        User targetUser = userRepository.findUserById(userUpdateRequestDTO.getUserId());
        BinaryContents profileImg = binaryContentsRepository.getBinaryContentsByBinaryContentsId(targetUser.getProfileId());

        return profileImg.getBinaryContentsPath().equals(userUpdateRequestDTO.getNewProfileImagePath());
    }
}
