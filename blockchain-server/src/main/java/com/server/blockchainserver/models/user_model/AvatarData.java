package com.server.blockchainserver.models.user_model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "avatar_image")
public class AvatarData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imgUrl;

    @OneToOne
    @JoinColumn(name = "userInfo_id")
    @JsonIgnore
    private UserInfo userInfo;

    public AvatarData() {
    }

    public AvatarData(String imgUrl, UserInfo userInfo) {
        this.imgUrl = imgUrl;
        this.userInfo = userInfo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
}
