package com.server.blockchainserver.dto.user_dto;

import com.server.blockchainserver.models.user_model.Role;
import com.server.blockchainserver.models.user_model.User;
import org.modelmapper.ModelMapper;

import java.util.HashSet;
import java.util.Set;

public class UserDTO {
    private Long userId;
    private UserInfoDTO userInfoDTO;
    private Set<Role> roles = new HashSet<>();
    private boolean isActive = true;
    private boolean emailVerified;
    public UserDTO(){

    }
    public UserDTO(User user, UserInfoDTO userInfoDTO) {
        ModelMapper mapper = new ModelMapper();
//        mapper.getConfiguration().setPropertyMappingStrategy(PropertyMappingStrategy.IGNORE_NULL); // Ignore null properties
        mapper.map(user, this);
        this.userInfoDTO = userInfoDTO;
    }


    public UserDTO(UserInfoDTO userInfoDTO, Long userId, Set<Role> roles, boolean isActive, boolean emailVerified) {
        this.userInfoDTO = userInfoDTO;
        this.userId = userId;
        this.roles = roles;
        this.isActive = isActive;
        this.emailVerified = emailVerified;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public UserInfoDTO getUserInfo() {
        return userInfoDTO;
    }

    public void setUserInfoDTO(UserInfoDTO userInfoDTO) {
        this.userInfoDTO = userInfoDTO;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }
}
