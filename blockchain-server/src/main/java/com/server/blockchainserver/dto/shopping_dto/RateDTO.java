package com.server.blockchainserver.dto.shopping_dto;

import com.server.blockchainserver.dto.user_dto.UserDTO;
import com.server.blockchainserver.models.shopping_model.Rate;
import org.modelmapper.ModelMapper;

import java.time.Instant;
import java.time.LocalDateTime;

public class RateDTO {
    private Long id;
    private String content;
    private Instant createDate;
    private Instant updateDate;
    private boolean isHide;
    private String imgUrl;
    private UserDTO userDTO;
    public RateDTO() {

    }

    public RateDTO(Rate rate, UserDTO userDTO) {
        ModelMapper mapper = new ModelMapper();
//        mapper.getConfiguration().setPropertyMappingStrategy(PropertyMappingStrategy.IGNORE_NULL); // Ignore null properties
        mapper.map(rate, this);
        this.userDTO = userDTO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public UserDTO getUser() {
        return userDTO;
    }

    public void setUserDTO(UserDTO userDTO) {
        this.userDTO = userDTO;
    }
}
