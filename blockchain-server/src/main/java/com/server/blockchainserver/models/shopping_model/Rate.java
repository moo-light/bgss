package com.server.blockchainserver.models.shopping_model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.server.blockchainserver.models.user_model.User;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "rates")
public class Rate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    @JsonIgnore
    private Post post;
    @Column(columnDefinition = "TEXT", length = 2000)
    private String content;
    private Instant createDate;
    private Instant updateDate;
    private boolean isHide;
    private String imgUrl;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Rate() {
    }

    public Rate(Post post, String content, Instant createDate, Instant updateDate, boolean isHide, String imgUrl, User user) {
        this.post = post;
        this.content = content;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.isHide = isHide;
        this.imgUrl = imgUrl;
        this.user = user;
    }

    public Rate(Post post, String content, Instant createDate, boolean isHide,User user) {
        this.post = post;
        this.content = content;
        this.createDate = createDate;
        this.isHide = isHide;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Instant createDate) {
        this.createDate = createDate;
    }

    public Instant getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Instant updateDate) {
        this.updateDate = updateDate;
    }

    public boolean isHide() {
        return isHide;
    }

    public void setHide(boolean hide) {
        isHide = hide;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
