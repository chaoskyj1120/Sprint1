package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class BinaryContents extends BaseEntity {
    private UUID referenceId;
    private final BinaryContentType binaryContentType;
    private final byte[] binaryData;

    public BinaryContents(BinaryContentType binaryContentType, byte[] binaryData) {
        this.binaryContentType = binaryContentType;
        this.binaryData = binaryData;
    }

    public void setReferenceId(UUID referenceId) {
        this.referenceId = referenceId;
    }
    // 생성자에서 주입 안하는 이유는 유저를 생성할려면 프로필 아이디를 먼저 생성해야하기 때문
}

// 이것이 메시지를 위한 바이너리 컨텐츠인지 유저 프로필을 위한 바이너리 컨텐츠인지 어떻게 알지? -> enum 으로
