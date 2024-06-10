package com.server.blockchainserver.controllers.user_controller;

import com.server.blockchainserver.advices.Response;
import com.server.blockchainserver.dto.user_dto.UserDtos;
import com.server.blockchainserver.dto.user_dto.UserInfoDTOs;
import com.server.blockchainserver.exeptions.NotFoundException;
import com.server.blockchainserver.exeptions.UserInfoException;
import com.server.blockchainserver.models.user_model.AvatarData;
import com.server.blockchainserver.models.user_model.CICardImage;
import com.server.blockchainserver.models.user_model.User;
import com.server.blockchainserver.payload.request.user_request.UserInfoRequest;
import com.server.blockchainserver.payload.response.InformationUserResponse;
import com.server.blockchainserver.security.userServices.UserService;
import com.server.blockchainserver.server.UserInfoManagement;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/auth")
public class UserInfoController {

    @Autowired
    private UserService userService;

    @Autowired
    UserInfoManagement userInfoManagement;

    @PostMapping("/create-user-info")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> createUserInfo(
            @RequestBody UserInfoRequest userInfoRequest) {
        try {
            Response response = new Response(HttpStatus.OK, "Create user info success.", userService.createUserInfo(userInfoRequest));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            Response response = new Response(HttpStatus.BAD_REQUEST, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred." + e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update-user-info/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CUSTOMER') or hasRole('ROLE_STAFF')")
    @Transactional
    public ResponseEntity<Response> updateUserInfo(@PathVariable Long userId, @RequestBody UserInfoRequest userInfoRequest) {
        try {
            UserInfoDTOs userInfo = userService.updateUserInfor(userId, userInfoRequest);
            Response response = new Response(HttpStatus.OK, "Update user info success.", userInfo);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (UserInfoException e) {
            Response response = new Response(HttpStatus.BAD_REQUEST, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (NotFoundException e) {
            Response response = new Response(HttpStatus.NOT_FOUND, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred." + e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/show-user-info/{id}")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')" + " or hasRole('ROLE_STAFF')"
            + " or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> showUserInfo(@PathVariable Long id) {
        try {
            UserInfoDTOs userInfo = userService.getUserInfo(id);
            UserDtos user = userService.getUserById(id);
            InformationUserResponse info = new InformationUserResponse(user.getId(), user.getUsername(), user.getEmail(), userInfo);

            Response response = new Response(HttpStatus.OK, "Show user information success.", info);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred." + e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update-avatar/{userInfoId}")
    @PreAuthorize("hasRole('ROLE_CUSTOMER') " +
            "or hasRole('ROLE_STAFF') " +
            "or hasRole('ROLE_ADMIN')")
    @Transactional
    public ResponseEntity<Response> avatarUpdate(@PathVariable Long userInfoId, @NotNull @RequestBody MultipartFile imageData) {
        try {
            AvatarData userImage = userService.avatarUpdate(userInfoId, imageData);
            Response response = new Response(HttpStatus.OK, "Update avatar of user success.", userImage);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (NotFoundException e) {
            Response response = new Response(HttpStatus.NOT_FOUND, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred." + e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update-cicard-image/{id}")
    @PreAuthorize("hasRole('ROLE_CUSTOMER') " +
            "or hasRole('ROLE_STAFF') " +
            "or hasRole('ROLE_ADMIN')")
    @Transactional
    public ResponseEntity<Response> ciCardUpdate(@PathVariable Long id, @NotNull @RequestBody MultipartFile[] ciCardImages) {
        try {
            List<CICardImage> images = userService.ciCardUpdate(id, ciCardImages);
            Response response = new Response(HttpStatus.OK, "Upload CICard of user success.", images);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (NotFoundException e) {
            Response response = new Response(HttpStatus.NOT_FOUND, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred." + e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
