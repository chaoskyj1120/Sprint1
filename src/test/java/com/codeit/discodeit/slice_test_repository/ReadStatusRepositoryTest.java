package com.codeit.discodeit.slice_test_repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.codeit.discodeit.entity.Channel;
import com.codeit.discodeit.entity.ReadStatus;
import com.codeit.discodeit.entity.User;
import com.codeit.discodeit.entity.UserStatus;
import com.codeit.discodeit.repository.ChannelRepository;
import com.codeit.discodeit.repository.ReadStatusRepository;
import com.codeit.discodeit.repository.UserRepository;
import com.codeit.discodeit.repository.UserStatusRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test") // yaml 파일을 test로
//@EnableJpaAuditing // Main에 이미 적용되어서 주석처리를 통해 Bean 중복 등록을 피함
class ReadStatusRepositoryTest {

  @Autowired
  private UserStatusRepository userStatusRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ChannelRepository channelRepository;
  @Autowired
  private ReadStatusRepository readStatusRepository;

  @Test
  void 채널_아이디와_유저_아이디로_읽기상태_검색_테스트_성공() {
    // given
    User user = new User();
    user.setUsername("kwon");
    user.setEmail("kwon@example.com");
    user.setPassword("pw1");
    userRepository.save(user);

    Channel channel = new Channel();
    channel.setName("test");
    channelRepository.save(channel);

    ReadStatus readStatus = new ReadStatus();
    readStatus.setUser(user);
    readStatus.setChannel(channel);
    readStatusRepository.save(readStatus);

    // when
    Optional<ReadStatus> targetReadStatus = readStatusRepository.findByUserIdAndChannelId(user.getId(), channel.getId());

    // then
    assertThat(targetReadStatus.isPresent()).isTrue();
  }
}
