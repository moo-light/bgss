package com.server.blockchainserver.dto.shopping_dto;

import com.server.blockchainserver.dto.user_dto.UserDTO;
import com.server.blockchainserver.models.shopping_model.ReviewProduct;
import com.server.blockchainserver.models.user_model.User;
import org.modelmapper.ModelMapper;

import java.time.Instant;

public class ReviewProductDTO {
    private Long id;
    private int numOfReviews;
    private String content;
    private Instant createDate;
    private Instant updateDate;
    private String imgUrl;
    private UserDTO userDTO;
    public  ReviewProductDTO(){

    }
    public ReviewProductDTO(ReviewProduct reviewProduct, UserDTO user) {
        ModelMapper mapper = new ModelMapper();
        mapper.map(reviewProduct, this);
        this.userDTO = user;
    }

    public ReviewProductDTO(Long id, int numOfReviews, String content, Instant createDate, Instant updateDate, String imgUrl, UserDTO userDTO) {
        this.id = id;
        this.numOfReviews = numOfReviews;
        this.content = content;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.imgUrl = imgUrl;
        this.userDTO = userDTO;
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

    public UserDTO getUser() {
        return userDTO;
    }

    public void setUserDTO(UserDTO userDTO) {
        this.userDTO = userDTO;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
