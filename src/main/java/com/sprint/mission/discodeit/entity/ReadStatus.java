package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class ReadStatus extends BaseEntity {

    private final UUID channelId;
    private final UUID userId;
    private UUID messageId;

    public ReadStatus(UUID channelId, UUID userId) {
        this.channelId = channelId;
        this.userId = userId;
    }

    // 마지막 메세지가 달라질 때 setter로 messageId 변경 -> 채널을 퇴장할 때
}

// 사용자가 채널 별 마지막으로 메시지를 읽은 시간을 표현하는 도메인 모델입니다.
// 사용자별 각 채널에 읽지 않은 메시지를 확인하기 위해 활용합니다.
// 채널 입장과 퇴장을 구현해야 함