package com.codeit.discodeit.service;

import com.codeit.discodeit.fakerepository.FakeChannelRepository;
import com.codeit.discodeit.fakerepository.FakeUserRepository;
import com.codeit.discodeit.fakerepository.FakeUserStatusRepository;
import com.codeit.discodeit.mapper.UserMapper;
import com.codeit.discodeit.repository.ChannelRepository;
import com.codeit.discodeit.repository.UserRepository;
import com.codeit.discodeit.repository.UserStatusRepository;
import com.codeit.discodeit.service.basic.BasicUserService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

public class ChannelServiceTest {
  private UserRepository fakeUserRepository;
  private ChannelRepository fakeChannelRepository;
  private UserStatusRepository fakeUserStatusRepository;

  private ReadStatusService readStatusService;
  private UserStatusService userStatusService;
  private BinaryContentService binaryContentService;
  private com.codeit.discodeit.mapper.UserMapper userMapper;

  private BasicUserService BasicChannelService;

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
}
