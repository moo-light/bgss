package com.server.blockchainserver.security.userServices;

import com.server.blockchainserver.dto.user_dto.UserDtos;
import com.server.blockchainserver.dto.user_dto.UserInfoDTOs;
import com.server.blockchainserver.models.user_model.AvatarData;
import com.server.blockchainserver.models.user_model.CICardImage;
import com.server.blockchainserver.models.user_model.User;
import com.server.blockchainserver.models.user_model.UserInfo;
import com.server.blockchainserver.payload.request.user_request.UserInfoRequest;
import com.server.blockchainserver.server.UserInfoManagement;
import com.server.blockchainserver.server.UserManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.List;

@Service
public class UserServiceImpl implements UserService{

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserManagement userManagement;

    @Autowired
    private UserInfoManagement userInfoManagement;

    @Override
    public List<UserDtos> showListUser(String search) {
        return userManagement.showListUser(search);
    }

    @Override
    public UserInfoDTOs updateUserInfor(Long userId, UserInfoRequest userInfoRequest) throws ParseException, NoSuchAlgorithmException {
        return userInfoManagement.updateUserInfo(userId, userInfoRequest);
    }

    @Override
    public UserDtos getUserById(Long id){
        return userManagement.getUserById(id);
    }

    @Override
    public UserDtos lockOrActiveUser(Long id, boolean isActive){
        return userManagement.lockOrActiveUser(id, isActive);
    }

//    @Override
//    public List<UserDtos> searchUserByName(String search){
//        return userManagement.searchUserByName(search);
//    }

    @Override
    public UserDtos changePassword(Long userId, String oldPwd, String newPwd, String confirmNewPwd) throws Exception {
        return userManagement.changePassword(userId, oldPwd, newPwd, confirmNewPwd);
    }

    @Override
    public UserInfoDTOs getUserInfo(Long id) {
        return userInfoManagement.getUserInfo(id);
    }

    @Override
    public UserInfo createUserInfo(UserInfoRequest userInfoRequest) {
        return userInfoManagement.createUserInfo(userInfoRequest);
    }

    @Override
    public AvatarData avatarUpdate(Long userInfoId, MultipartFile avatarFile) throws IOException {
        return userInfoManagement.avatarUpdate(userInfoId, avatarFile);
    }

    @Override
    public List<CICardImage> ciCardUpdate(Long id, MultipartFile[] images) throws IOException {
        return userInfoManagement.ciCardImage(id, images);
    }

//    @Override
//    public BigDecimal getBalance(Long userId) {
//        return userManagement.getBalance(userId);
//    }
}
