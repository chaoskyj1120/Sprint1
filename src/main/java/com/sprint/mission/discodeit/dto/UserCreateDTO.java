package com.sprint.mission.discodeit.dto;

import lombok.Getter;

@Getter
public class UserCreateDTO {

    private String idForLogin;
    private String password;
    private String name;
    private String email;
    private String profilePicturePath;

    public UserCreateDTO(String idForLogin, String password, String name, String email, String profilePicturePath) {
        this.idForLogin = idForLogin;
        this.password = password;
        this.name = name;
        this.email = email;
        this.profilePicturePath = profilePicturePath;
    }
}
