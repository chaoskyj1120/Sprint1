package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class BinaryContents extends BaseEntity {
    private UUID referenceId;
    private String binaryContentsPath;
    private final BinaryContentType binaryContentType;
    private final byte[] binaryData;

    public BinaryContents(String binaryContentsPath, BinaryContentType binaryContentType, byte[] binaryData) {
        this.binaryContentsPath = binaryContentsPath;
        this.binaryContentType = binaryContentType;
        this.binaryData = binaryData;
    }
    // 생성자에서 주입 안하는 이유는 유저를 생성할려면 프로필 아이디를 먼저 생성해야하기 때문
}

// 이것이 메시지를 위한 바이너리 컨텐츠인지 유저 프로필을 위한 바이너리 컨텐츠인지 어떻게 알지? -> enum 으로
