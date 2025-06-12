package com.sprint.mission.discodeit.run;

import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;

public class TestRepository {
    public static void main(String[] args) {
        //FileMessageRepository.getInstance().printAllMessage();
        FileChannelRepository.getInstance().printAllChannels();
        FileUserRepository.getInstance().printAllUsers();
    }
}
