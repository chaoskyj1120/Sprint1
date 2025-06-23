package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus extends BaseEntity{

    private final UUID userId; // 사용자의 Id
    private final boolean loggedIn; // true면 로그인 상태, false면 로그아웃 상태
    
    public UserStatus(User user, boolean loggedIn) {
        super();
        this.userId = user.getId();
        this.loggedIn = loggedIn;
    }

    public String getLoggedIn() {
        // 현재 시간과 변경된 시간의 차이가 5분내면 "로그인" 아니면 "로그아웃"을 반환
        Duration duration = Duration.between(getUpdatedAt(), Instant.now());
        long minutes = duration.toMinutes();
        return minutes < 5 ? "로그인" : "로그아웃";
    }
}

/*
 * 사용자 별 마지막으로 확인된 접속 시간을 표현하는 도메인 모델입니다. 사용자의 온라인 상태를 확인하기 위해 활용합니다.
 * [ ] 마지막 접속 시간을 기준으로 현재 로그인한 유저로 판단할 수 있는 메소드를 정의하세요.
 * 마지막 접속 시간이 현재 시간으로부터 5분 이내이면 현재 접속 중인 유저로 간주합니다.
 */
