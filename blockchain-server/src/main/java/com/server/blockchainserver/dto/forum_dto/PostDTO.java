package com.server.blockchainserver.dto.forum_dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.server.blockchainserver.dto.user_dto.UserDTO;
import com.server.blockchainserver.models.shopping_model.CategoryPost;
import com.server.blockchainserver.models.shopping_model.Post;
import com.server.blockchainserver.models.shopping_model.Rate;
import org.modelmapper.ModelMapper;

import java.time.Instant;
import java.util.List;

public class PostDTO {
    private Long id;
    private String title;
    private String content;
    private Instant createDate;
    private Instant updateDate;
    private Instant deleteDate;
    private String imgUrl;
    private boolean isPinned;
    private boolean isHide;

    private CategoryPost categoryPost;

    @JsonIgnore
    private List<Rate> rates;
    private UserDTO userDTO;

    public PostDTO() {
    }

    public PostDTO(Post post, UserDTO userDTO) {
        ModelMapper mapper = new ModelMapper();
//        mapper.getConfiguration().setPropertyMappingStrategy(PropertyMappingStrategy.IGNORE_NULL); // Ignore null properties
        mapper.map(post, this);
        this.userDTO = userDTO;
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

    public String getTextImg() {
        return imgUrl;
    }

    public void setImgUrl(String textImg) {
        this.imgUrl = textImg;
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

    public int getRateCount() {
        if (rates == null) {
            return 0;
        }
        return rates.size();
    }

    public UserDTO getUser() {
        return userDTO;
    }

    public void setUserDTO(UserDTO userDTO) {
        this.userDTO = userDTO;
    }
}
