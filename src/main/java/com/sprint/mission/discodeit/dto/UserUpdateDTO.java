package com.sprint.mission.discodeit.dto;

import lombok.Getter;

import java.util.UUID;

@Getter
public class UserUpdateDTO {
    private UUID userId;

    private String newUserIdForLogin;
    private String newName;
    private String newPassword;
    private String newProfileImagePath;
    private String newEmail;

    public UserUpdateDTO(UUID userId, String newUserIdForLogin, String newNickname, String newPassword, String newEmail, String newProfileImagePath) {
        this.userId = userId;

        this.newUserIdForLogin = newUserIdForLogin;
        this.newName = newNickname;
        this.newPassword = newPassword;
        this.newEmail = newEmail;
        this.newProfileImagePath = newProfileImagePath;
    }

    // Getters & Setters
}
