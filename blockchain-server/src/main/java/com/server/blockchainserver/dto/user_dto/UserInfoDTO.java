package com.server.blockchainserver.dto.user_dto;

import com.server.blockchainserver.models.user_model.AvatarData;
import com.server.blockchainserver.models.user_model.UserInfo;
import org.modelmapper.ModelMapper;

import java.sql.Date;

public class UserInfoDTO {
    private Long userInfoId;
    private String firstName;
    private String lastName;
    private Date doB;
    private AvatarData avatar;

    public UserInfoDTO(){

    }
    public UserInfoDTO(UserInfo userInfo) {
        ModelMapper mapper = new ModelMapper();
        mapper.map(userInfo, this);
    }

    public UserInfoDTO(String lastName, Long userInfoId, String firstName, Date doB, AvatarData avatar) {
        this.lastName = lastName;
        this.userInfoId = userInfoId;
        this.firstName = firstName;
        this.doB = doB;
        this.avatar = avatar;
    }

    public Long getUserInfoId() {
        return userInfoId;
    }

    public void setUserInfoId(Long userInfoId) {
        this.userInfoId = userInfoId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getDoB() {
        return doB;
    }

    public void setDoB(Date doB) {
        this.doB = doB;
    }

    public AvatarData getAvatar() {
        return avatar;
    }

    public void setAvatar(AvatarData avatar) {
        this.avatar = avatar;
    }
}
