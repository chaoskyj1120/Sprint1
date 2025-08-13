package com.codeit.discodeit.service;

import com.codeit.discodeit.dto.user_service_dto.UserCreateRequest;
import com.codeit.discodeit.dto.user_service_dto.UserDto;
import com.codeit.discodeit.dto.user_service_dto.UserUpdateRequest;
import com.codeit.discodeit.entity.BinaryContent;
import com.codeit.discodeit.entity.User;
import com.codeit.discodeit.entity.UserStatus;
import com.codeit.discodeit.exception.user.UserNameEmailDuplicateException;
import com.codeit.discodeit.exception.user.UserNotFoundException;
import com.codeit.discodeit.fakerepository.FakeChannelRepository;
import com.codeit.discodeit.fakerepository.FakeUserRepository;
import com.codeit.discodeit.fakerepository.FakeUserStatusRepository;
import com.codeit.discodeit.mapper.UserMapper;
import com.codeit.discodeit.repository.ChannelRepository;
import com.codeit.discodeit.repository.UserRepository;
import com.codeit.discodeit.repository.UserStatusRepository;
import com.codeit.discodeit.service.basic.BasicUserService;
import java.io.IOException;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.function.Executable;


public class UserServiceTest {

  private UserRepository fakeUserRepository;
  private ChannelRepository fakeChannelRepository;
  private UserStatusRepository fakeUserStatusRepository;

  private ReadStatusService readStatusService;
  private UserStatusService userStatusService;
  private BinaryContentService binaryContentService;
  private com.codeit.discodeit.mapper.UserMapper userMapper;

  private BasicUserService userService;

  @BeforeEach
  void setUp() {
    // Fake Repository 생성
    fakeUserRepository = new FakeUserRepository();
    fakeChannelRepository = new FakeChannelRepository();
    fakeUserStatusRepository = new FakeUserStatusRepository();

    // Mock Service 생성
    userStatusService = Mockito.mock(UserStatusService.class);
    binaryContentService = Mockito.mock(BinaryContentService.class);
    readStatusService = Mockito.mock(ReadStatusService.class);

    userMapper = Mockito.mock(UserMapper.class);

    // 테스트 대상 서비스 생성
    userService = new BasicUserService(
        fakeUserRepository,
        fakeChannelRepository,
        readStatusService,
        userStatusService,
        binaryContentService,
        fakeUserStatusRepository,
        userMapper
    );
  }

  @Test
  void 유저_생성_성공_테스트() throws IOException {
    // given
    UserCreateRequest request = new UserCreateRequest("kwon1", "pw1", "kwon1@email.com", null);

    User mockUser = new User();
    mockUser.setId(UUID.randomUUID());
    mockUser.setUsername(request.getUsername());
    mockUser.setEmail(request.getEmail());
    mockUser.setPassword(request.getPassword());
    mockUser.setCreatedAt(Instant.now());
    Mockito.when(userMapper.toUser(Mockito.any(UserCreateRequest.class)))
        .thenReturn(mockUser); //

    UserStatus mockStatus = new UserStatus();
    mockStatus.setUser(mockUser); // mockUser는 userMapper.toUser(...)로 만든 User 객체
    mockStatus.setId(UUID.randomUUID());
    mockUser.setStatus(mockStatus);
    Mockito.when(userStatusService.createUserStatus(Mockito.any(User.class)))
        .thenReturn(mockStatus);

    UserDto mockDto = new UserDto(mockUser.getId(), mockUser.getUsername(), mockUser.getEmail(), null, true);
    Mockito.when(userMapper.toUserDto(Mockito.any(User.class)))
        .thenReturn(mockDto);
    // when
    UserDto result = userService.createUser(request);
    // then
    assertEquals(1, fakeUserRepository.loadUsers().size());
    Mockito.verify(userStatusService).createUserStatus(Mockito.any(User.class));
    Mockito.verify(binaryContentService).createByteFile(Mockito.any(), Mockito.any());
  }

  @Test
  void 유저네임이_중복될_때_유저_생성_실패_테스트() throws IOException {
    // given
    UserCreateRequest request = new UserCreateRequest("kwon1", "pw1", "kown1@email.com", null);

    // fakeUserRepository에 이미 같은 username을 가진 User 추가
    User existing = new User();
    existing.setId(UUID.randomUUID());
    existing.setUsername("kwon1");
    existing.setEmail("kwon123@email.com");
    fakeUserRepository.createUser(existing);

    // when
    Executable action = () -> userService.createUser(request);

    // then
    assertThrows(UserNameEmailDuplicateException.class, action);
  }

  @Test
  void 이메일이_중복될_때_유저_생성_실패_테스트() throws IOException {
    // given
    UserCreateRequest request = new UserCreateRequest("kwon1", "pw1", "kwon1@email.com", null);

    // fakeUserRepository에 이미 같은 email을 가진 User 추가
    User existing = new User();
    existing.setId(UUID.randomUUID());
    existing.setUsername("kwon2");
    existing.setEmail("kwon1@email.com");
    fakeUserRepository.createUser(existing);

    // when
    Executable action = () -> userService.createUser(request);

    // then
    assertThrows(UserNameEmailDuplicateException.class, action);
  }

  @Test
  void 기본_정보_유저_업데이트_성공_테스트() {
    // given
    UUID userId = UUID.randomUUID();
    User targetUser = new User();
    targetUser.setId(userId);
    targetUser.setUsername("oldUsername");
    targetUser.setEmail("old@email.com");
    targetUser.setPassword("oldPassword");

    UserUpdateRequest request = new UserUpdateRequest(
        userId,
        "newUsername",
        "new@email.com",
        "newPassword"
    );

    fakeUserRepository.createUser(targetUser);

    Mockito.when(userMapper.toUserDto(Mockito.any(User.class))).thenAnswer(i -> {
      User u = i.getArgument(0);
      return new UserDto(u.getId(), u.getUsername(), u.getEmail(), null, true);
    });

    // when
    UserDto result = userService.updateUser(request, null, null);

    // then
    assertEquals("newUsername", result.username());
    assertEquals("new@email.com", result.email());
  }

  @Test
  void 프로필_이미지_정보_유저_업데이트_성공_테스트() {
    // given
    UUID userId = UUID.randomUUID();
    User targetUser = new User();
    targetUser.setId(userId);
    targetUser.setUsername("user");
    targetUser.setEmail("email");
    targetUser.setPassword("pw");

    BinaryContent oldProfile = new BinaryContent();
    oldProfile.setFileName("old.png");
    oldProfile.setSize(100L);
    oldProfile.setContentType("image/png");
    targetUser.setProfile(oldProfile);
    fakeUserRepository.createUser(targetUser); // 기존 유저 생성

    BinaryContent newProfile = new BinaryContent();
    newProfile.setFileName("new.png");
    newProfile.setSize(200L);
    newProfile.setContentType("image/png");
    
    UserUpdateRequest request = new UserUpdateRequest(userId, "user", "email", "pw");

    Mockito.when(userMapper.toUserDto(Mockito.any(User.class))).thenAnswer(i -> {
      User u = i.getArgument(0);
      return new UserDto(u.getId(), u.getUsername(), u.getEmail(), null, true);
    });

    byte[] dummyBytes = new byte[]{1,2,3};

    // when
    UserDto result = userService.updateUser(request, newProfile, dummyBytes);

    // then
    assertEquals(newProfile.getFileName(), targetUser.getProfile().getFileName());
  }

  @Test
  void 유저_업데이트_할_때_유저가_없어_실패_테스트(){
    //given
    UserUpdateRequest request = new UserUpdateRequest(UUID.randomUUID(), "user", "email", "pw");
    // when
    Executable action = () -> userService.updateUser(request, null, null);
    // then
    assertThrows(UserNotFoundException.class, action);
  }

  @Test
  void 유저_삭제_성공_테스트(){
    // given
    UUID userId = UUID.randomUUID();
    User targetUser = new User();
    targetUser.setId(userId);
    targetUser.setUsername("oldUsername");
    targetUser.setEmail("old@email.com");
    targetUser.setPassword("oldPassword");

    fakeUserRepository.createUser(targetUser); // 삭제할 유저 생성

    // when
    userService.deleteUser(userId);

    // then
    assertEquals(0, fakeUserRepository.loadUsers().size());
  }

  @Test
  void 유저가_없을_때_유저_삭제_실패_테스트(){
    // given
    UUID userId = UUID.randomUUID();
    // when
    Executable action = () -> userService.deleteUser(userId);
    // then
    assertThrows(UserNotFoundException.class, action);
  }

}