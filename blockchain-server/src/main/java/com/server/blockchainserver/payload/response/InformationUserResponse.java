package com.server.blockchainserver.payload.response;

import com.server.blockchainserver.dto.user_dto.UserInfoDTOs;

public class InformationUserResponse {

    private long userId;
    private String username;
    private String email;
    private UserInfoDTOs userInfo;
//    UserInfo userInfo;

    public InformationUserResponse() {
    }

    public InformationUserResponse(long userId, String username, String email, UserInfoDTOs userInfoDTOs) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.userInfo = userInfoDTOs;
    }


    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserInfoDTOs getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfoDTOs userInfo) {
        this.userInfo = userInfo;
    }
}
