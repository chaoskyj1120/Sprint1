package com.sprint.mission.discodeit.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {
    private String idForLogin;
    private String passwordForLogin;

    public LoginRequestDTO(String idForLogin, String passwordForLogin) {
        this.idForLogin = idForLogin;
        this.passwordForLogin = passwordForLogin;
    }
}
