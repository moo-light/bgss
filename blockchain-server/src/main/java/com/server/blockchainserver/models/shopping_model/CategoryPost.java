package com.server.blockchainserver.models.shopping_model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "category_forums")
public class CategoryPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Size(max = 100)
    private String categoryName;
    @ManyToOne
    @JoinColumn(name = "forum_id")
    private Forum forum;

    private boolean active;
    public CategoryPost() {
    }

    public CategoryPost(String categoryName, Forum forum, boolean active) {
        this.categoryName = categoryName;
        this.forum = forum;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Forum getForums() {
        return forum;
    }

    public void setForums(Forum forums) {
        this.forum = forums;
    }
//
//    public List<Post> getPosts() {
//        return posts;
//    }
//
//    public void setPosts(List<Post> posts) {
//        this.posts = posts;
//    }


    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
