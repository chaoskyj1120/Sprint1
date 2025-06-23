package com.sprint.mission.discodeit.dto;

import lombok.Getter;

import java.util.UUID;

@Getter
public class UserLoginDataDTO {
    private UUID id;
    private String userId;
    private String userName;
    private String userEmail;
    private byte[] userPicture;


    public UserLoginDataDTO(UUID id, String userId, String userName, String userEmail, byte[] userPicture) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPicture = userPicture;
    }

}
