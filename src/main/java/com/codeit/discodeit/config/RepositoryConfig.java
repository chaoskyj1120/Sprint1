package com.codeit.discodeit.config;

import com.codeit.discodeit.repository.*;
import com.codeit.discodeit.repository.file.*;
import com.codeit.discodeit.repository.jcf.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RepositoryConfig {

    private final DiscodeitRepositoryProperties properties;

    private boolean useFile() {
        return "file".equalsIgnoreCase(properties.getType());
    }

    @Bean
    public UserRepository userRepository() {
        return useFile() ?
                new FileUserRepository(properties.getFileDirectory()) :
                new JcfUserRepository();
    }

    @Bean
    public BinaryContentRepository binaryContentsRepository() {
        return useFile() ?
                new FileBinaryContentRepository(properties.getFileDirectory()) :
                new JcfBinaryContentRepository();
    }

    @Bean
    public ChannelRepository channelRepository() {
        return useFile() ?
                new FileChannelRepository(properties.getFileDirectory()) :
                new JcfChannelRepository();
    }

    @Bean
    public MessageRepository messageRepository() {
        return useFile() ?
                new FileMessageRepository(properties.getFileDirectory()) :
                new JcfMessageRepository();
    }

    @Bean
    public ReadStatusRepository readStatusRepository() {
        return useFile() ?
                new FileReadStatusRepository(properties.getFileDirectory()) :
                new JcfReadStatusRepository();
    }

    @Bean
    public UserStatusRepository userStatusRepository() {
        return useFile() ?
                new FileUserStatusRepository(properties.getFileDirectory()) :
                new JcfUserStatusRepository();
    }
}