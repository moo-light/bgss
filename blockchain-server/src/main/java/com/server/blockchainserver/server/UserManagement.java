package com.server.blockchainserver.server;

import com.server.blockchainserver.dto.user_dto.UserDtos;
import com.server.blockchainserver.dto.user_dto.UserInfoDTOs;
import com.server.blockchainserver.exeptions.NotFoundException;
import com.server.blockchainserver.exeptions.UserException;
import com.server.blockchainserver.models.enums.ERole;
import com.server.blockchainserver.models.user_model.User;
import com.server.blockchainserver.repository.user_repository.AvatarDataRepository;
import com.server.blockchainserver.repository.user_repository.CICardRepository;
import com.server.blockchainserver.repository.user_repository.UserInfoRepository;
import com.server.blockchainserver.repository.user_repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserManagement {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserInfoRepository userInfoRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserInfoManagement userInfoManagement;
    @Autowired
    AvatarDataRepository avatarDataRepository;
    @Autowired
    CICardRepository ciCardRepository;

    public List<UserDtos> showListUser(String search) {
        List<User> users;
        if (search == null || search.trim().isEmpty()) {
            users = userRepository.findAll();
        } else {
            String normalizedSearch = "%" + search.trim().toLowerCase() + "%";
            users = userRepository.findByFirstNameOrLastNameWithoutAccent(normalizedSearch);
        }

        List<User> userList = new ArrayList<>();
        for (User user : users) {
            if (!user.getRoleNames().contains("ROLE_ADMIN")) {
                userList.add(user);
            }
        }

        Collections.reverse(userList);
        // Chuyển đổi List<User> sang List<UserDTOs>
        return userList.stream().map(user -> {
            // Lấy thông tin chi tiết UserInfoDTOs cho mỗi User dựa trên userId
            UserInfoDTOs userInfoDTOs = userInfoManagement.getUserInfo(user.getId());

            // Tạo và trả về một đối tượng UserDTOs mới chứa đầy đủ thông tin
            return new UserDtos(
                    user.getId(),
                    user.getUsername(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    user.getPhoneNumber(),
                    user.getRoles(),
                    user.isActive(),
                    user.isEmailVerified(),
                    userInfoDTOs
            );
        }).collect(Collectors.toList());
    }




    @Transactional
    public UserDtos getUserById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Not found user with id: " + userId));
        UserInfoDTOs userInfoDTOs = userInfoManagement.getUserInfo(user.getId());
        return new UserDtos(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRoles(),
                user.isActive(),
                user.isEmailVerified(),
                userInfoDTOs
        );
    }


    public UserDtos changePassword(Long userId, String oldPwd, String newPwd, String confirmNewPwd) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Not found user with id: " + userId));

        if (!passwordEncoder.matches(oldPwd, user.getPassword())) {
            throw new UserException("Old password is incorrect.");
        }

        if (newPwd.equals(confirmNewPwd)) {
            user.setPassword(passwordEncoder.encode(newPwd));
        } else {
            throw new UserException("New password and confirm password mismatched.");
        }
        userRepository.save(user);
        UserInfoDTOs userInfoDTOs = userInfoManagement.getUserInfo(user.getId());
        return new UserDtos(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRoles(),
                user.isActive(),
                user.isEmailVerified(),
                userInfoDTOs
        );
    }

    public UserDtos lockOrActiveUser(Long userId, boolean isActive) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Not found user with id: " + userId));
        user.setActive(isActive);
        userRepository.save(user);
        UserInfoDTOs userInfoDTOs = userInfoManagement.getUserInfo(user.getId());
        return new UserDtos(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRoles(),
                user.isActive(),
                user.isEmailVerified(),
                userInfoDTOs
        );
    }

}
