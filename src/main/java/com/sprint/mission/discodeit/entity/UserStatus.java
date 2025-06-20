package com.sprint.mission.discodeit.entity;

import lombok.Getter;

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

    public boolean getLoggedIn() {
        return loggedIn;
    }

}

/*
 * 사용자 별 마지막으로 확인된 접속 시간을 표현하는 도메인 모델입니다. 사용자의 온라인 상태를 확인하기 위해 활용합니다.
 * [ ] 마지막 접속 시간을 기준으로 현재 로그인한 유저로 판단할 수 있는 메소드를 정의하세요.
 * 마지막 접속 시간이 현재 시간으로부터 5분 이내이면 현재 접속 중인 유저로 간주합니다.
 * 
 */

// 로그인 회원가입을 위한 아이디 비번이 필요함
// 사용자의 아이디와 해당 사용자의 로그인, 로그아웃 시점을 가지고 있다.
// 역대 로그인 기록을 확인할 수 있도록
// user 와 1대1 관계가 아님
// 해당 객체는 유저가 로그인 할 때마다 추가
// 상위 클래스의 createdAt을 로그인한 시점
// 상위 클래스의 updatedAt을 로그아웃한 시점으로 계산
// 로그인 할 때는 true로 생성하고 로그 아웃할 땐 false로 생성함