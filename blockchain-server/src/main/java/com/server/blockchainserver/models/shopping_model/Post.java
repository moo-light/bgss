package com.server.blockchainserver.models.shopping_model;

import com.server.blockchainserver.models.user_model.User;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    @Column(columnDefinition = "TEXT")
    private String content;
    private Instant createDate;
    private Instant updateDate;
    private Instant deleteDate;
    @Column(columnDefinition = "TEXT")
    private String imgUrl;
    private boolean isPinned;
    private boolean isHide;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "categoryPost_id")
    private CategoryPost categoryPost;
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private List<Rate> rates;

    public Post() {
    }

    public Post(String title, String content, Instant createDate, Instant updateDate, Instant deleteDate, String imgUrl, boolean isPinned, boolean isHide, CategoryPost categoryPost, User user) {
        this.title = title;
        this.content = content;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.deleteDate = deleteDate;
        this.imgUrl = imgUrl;
        this.isPinned = isPinned;
        this.isHide = isHide;
        this.categoryPost = categoryPost;
        this.user = user;
    }

    public Post(String title, String content, Instant createDate, boolean isPinned, boolean isHide, CategoryPost categoryPost, User user) {
        this.title = title;
        this.content = content;
        this.createDate = createDate;
        this.isPinned = isPinned;
        this.isHide = isHide;
        this.categoryPost = categoryPost;
        this.user = user;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Instant getDeleteDate() {
        return deleteDate;
    }

    public void setDeleteDate(Instant deleteDate) {
        this.deleteDate = deleteDate;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public boolean isPinned() {
        return isPinned;
    }

    public void setPinned(boolean pinned) {
        isPinned = pinned;
    }

    public boolean isHide() {
        return isHide;
    }

    public void setHide(boolean hide) {
        isHide = hide;
    }

    public CategoryPost getCategoryPost() {
        return categoryPost;
    }

    public void setCategoryPost(CategoryPost categoryPost) {
        this.categoryPost = categoryPost;
    }

    public List<Rate> getRates() {
        return rates;
    }

    public void setRates(List<Rate> rates) {
        this.rates = rates;
    }

    public enum FilterHideEnum {
        ALL, // show all
        HIDE, // hide == true
        DEFAULT, // hide == false
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
