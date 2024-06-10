package com.server.blockchainserver.security.userServices;

import com.server.blockchainserver.dto.user_dto.UserDtos;
import com.server.blockchainserver.dto.user_dto.UserInfoDTOs;
import com.server.blockchainserver.models.user_model.AvatarData;
import com.server.blockchainserver.models.user_model.CICardImage;
import com.server.blockchainserver.models.user_model.User;
import com.server.blockchainserver.models.user_model.UserInfo;
import com.server.blockchainserver.payload.request.user_request.UserInfoRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.List;

public interface UserService {

    UserInfoDTOs updateUserInfor(Long userId, UserInfoRequest userInfoRequest) throws ParseException, NoSuchAlgorithmException;

    List<UserDtos> showListUser(String search);

    UserDtos getUserById(Long id);

    UserDtos lockOrActiveUser(Long id, boolean isActive);

//    List<UserDtos> searchUserByName(String search);

    UserInfoDTOs getUserInfo(Long id);

    UserInfo createUserInfo(UserInfoRequest userInfoRequest);


    //    UserInfo avatarUpdate(Long id, UserInfoRequest userInfoRequest);
    AvatarData avatarUpdate(Long userInfoId, MultipartFile avatarFile) throws IOException;

    List<CICardImage> ciCardUpdate(Long id, MultipartFile[] images) throws IOException;

    UserDtos changePassword(Long userId, String oldPwd, String newPwd, String confirmNewPwd) throws Exception;

//    BigDecimal getBalance(Long userId);
}
