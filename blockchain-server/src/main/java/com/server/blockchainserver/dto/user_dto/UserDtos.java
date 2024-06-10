package com.server.blockchainserver.dto.user_dto;

import com.server.blockchainserver.models.user_model.Role;

import java.util.HashSet;
import java.util.Set;

public class UserDtos {

    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private Set<Role> roles = new HashSet<>();
    private boolean isActive = true;
    private boolean emailVerified = false;

    private UserInfoDTOs userInfo;

    public UserDtos(Long id, String username, String firstName, String lastName,
                    String email, String phoneNumber, Set<Role> roles,
                    boolean isActive, boolean emailVerified, UserInfoDTOs userInfoDTOs) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.roles = roles;
        this.isActive = isActive;
        this.userInfo = userInfoDTOs;
        this.emailVerified = emailVerified;
    }

    public UserDtos() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public UserInfoDTOs getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfoDTOs userInfo) {
        this.userInfo = userInfo;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }
}
