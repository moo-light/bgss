package com.server.blockchainserver.models.shopping_model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.server.blockchainserver.models.user_model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.core.annotation.Order;

import java.time.Instant;

@Entity
@Table(name = "review_products")
public class ReviewProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private int numOfReviews;

    @Column(columnDefinition = "TEXT")
    private String content;
    private Instant createDate;
    private Instant updateDate;
    private String imgUrl;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonIgnore
    private Product product;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    public ReviewProduct() {
    }

    public ReviewProduct(int numOfReviews, String content,Instant createDate, Product product, User user) {
        this.numOfReviews = numOfReviews;
        this.content = content;
        this.createDate = createDate;
        this.product = product;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getNumOfReviews() {
        return numOfReviews;
    }

    public void setNumOfReviews(int numOfReviews) {
        this.numOfReviews = numOfReviews;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Instant getCreateDate() {
        return createDate;
    }

    public Instant getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Instant updateDate) {
        this.updateDate = updateDate;
    }
}
