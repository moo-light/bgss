package com.server.blockchainserver.server;

import com.server.blockchainserver.dto.balance_dto.BalanceDTO;
import com.server.blockchainserver.dto.gold_inventory_dto.GoldTransactionDTO;
import com.server.blockchainserver.dto.user_dto.UserInfoDTOs;
import com.server.blockchainserver.exeptions.NotFoundException;
import com.server.blockchainserver.exeptions.ProductException;
import com.server.blockchainserver.exeptions.UserInfoException;
import com.server.blockchainserver.models.user_model.AvatarData;
import com.server.blockchainserver.models.user_model.CICardImage;
import com.server.blockchainserver.models.user_model.User;
import com.server.blockchainserver.models.user_model.UserInfo;
import com.server.blockchainserver.payload.request.user_request.UserInfoRequest;
import com.server.blockchainserver.platform.data_transfer_object.UserGoldInventoryDTO;
import com.server.blockchainserver.platform.entity.SecretKey;
import com.server.blockchainserver.platform.server.KeyGenerator;
import com.server.blockchainserver.repository.user_repository.*;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class UserInfoManagement {

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AvatarDataRepository avatarDataRepository;

    @Autowired
    private CICardRepository ciCardRepository;

    @Autowired
    private KeyGenerator keyGenerator;

    private static final Logger logger = LoggerFactory.getLogger(UserManagement.class);
    @Autowired
    private SecretKeyRepository secretKeyRepository;

    @Autowired
    private FPTAIVision fptaiVision;


    public void checkValidation(UserInfoRequest userInfoRequest){
        if(userInfoRequest.getFirstName() == null || userInfoRequest.getFirstName().isEmpty()){
            throw new UserInfoException("First Name can not be empty.");
        }
        if(userInfoRequest.getLastName() == null || userInfoRequest.getLastName().isEmpty()){
            throw new UserInfoException("Last Name can not be empty.");
        }
        if(userInfoRequest.getPhoneNumber() == null || userInfoRequest.getPhoneNumber().isEmpty()){
            throw new UserInfoException("Phone Number can not be empty.");
        }
        if(userInfoRequest.getAddress() == null || userInfoRequest.getAddress().isEmpty()){
            throw new UserInfoException("Address can not be empty.");
        }
        if(userInfoRequest.getDoB() == null){
            throw new UserInfoException("Address can not be empty.");
        }
        if (!Pattern.matches("[0-9.]+", userInfoRequest.getPhoneNumber())) {
            throw new UserInfoException("Phone Number can not contain character");
        }
        if(userInfoRequest.getPhoneNumber().length() != 10 ){
            throw new UserInfoException("Phone Number must have 10 numbers.");
        }
    }

    public UserInfoDTOs updateUserInfo(Long userId, UserInfoRequest userInfoRequest) throws ParseException, NoSuchAlgorithmException {
        checkValidation(userInfoRequest);
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Not found user with id: " + userId));
        UserInfo userInfo = user.getUserInfo();
        if (!userInfo.getPhoneNumber().equals(userInfoRequest.getPhoneNumber())) {
            userInfo.setPhoneNumber(userInfoRequest.getPhoneNumber());
        }
        userInfo.setFirstName(userInfoRequest.getFirstName());
        userInfo.setLastName(userInfoRequest.getLastName());

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String formatStr = formatter.format(userInfoRequest.getDoB());
        java.util.Date dobUtil = formatter.parse(formatStr);
        java.sql.Date dobSql = new java.sql.Date(dobUtil.getTime());
        userInfo.setDoB(dobSql);

        userInfo.setAddress(userInfoRequest.getAddress());
        userInfo.setCiCard(userInfoRequest.getCiCard());

        userInfoRepository.save(userInfo);

        user.setFirstName(userInfo.getFirstName());
        user.setLastName(userInfo.getLastName());
        user.setPhoneNumber(userInfo.getPhoneNumber());

        userRepository.save(user);
        Optional<SecretKey> secretKey = secretKeyRepository.findByUserInfo(userInfo);
        if (secretKey.isEmpty()) {
            keyGenerator.createNewSecretKeyForUser(userInfo.getId());
        }
        return getUserInfoDTOs(userInfo);
    }


    private static UserInfoDTOs getUserInfoDTOs(UserInfo userInfo) {
        UserInfoDTOs userInfoDTOs = new UserInfoDTOs();
        userInfoDTOs.setId(userInfo.getId());
        userInfoDTOs.setFirstName(userInfo.getFirstName());
        userInfoDTOs.setLastName(userInfo.getLastName());
        userInfoDTOs.setPhoneNumber(userInfo.getPhoneNumber());
        // Chuyển đổi AvatarData nếu cần
        userInfoDTOs.setAvatarData(userInfo.getAvatarData());
        userInfoDTOs.setDoB(userInfo.getDoB());
        userInfoDTOs.setAddress(userInfo.getAddress());
        userInfoDTOs.setCiCard(userInfo.getCiCard());
        return userInfoDTOs;
    }

    @Transactional
    public UserInfoDTOs getUserInfo(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        UserInfo userInfo = userInfoRepository.findUserInfoByUserId(user.get().getId());
        AvatarData avatarData = avatarDataRepository.findByUserInfoId(userInfo.getId());
        List<CICardImage> ciCardImages = ciCardRepository.findAllByUserInfoId(userInfo.getId());

        userInfo.setAvatarData(avatarData);
        userInfo.setCiCardImage(ciCardImages);
        user.get().setUserInfo(userInfo);
        BalanceDTO balanceDTO = new BalanceDTO(userInfo.getBalance().getId(), userInfo.getBalance().getBalance(),
                userInfo.getId());
        UserGoldInventoryDTO inventoryDTO = new UserGoldInventoryDTO(userInfo.getInventory());
        Set<GoldTransactionDTO> goldTransactionDTOs = userInfo.getGoldTransactions().stream()
                .map(GoldTransactionDTO::new)
                .collect(Collectors.toSet());

        return new UserInfoDTOs(userInfo.getId(),
                userInfo.getFirstName(),
                userInfo.getLastName(),
                userInfo.getPhoneNumber(),
                avatarData,
                userInfo.getDoB(),
                userInfo.getAddress(),
                userInfo.getCiCard(),
                ciCardImages,
                balanceDTO,
                inventoryDTO,
                goldTransactionDTOs);
    }


    public UserInfo createUserInfo(UserInfoRequest userInfoRequest) {
        UserInfo userInfo = new UserInfo();
        userInfo.setFirstName(userInfoRequest.getFirstName());
        userInfo.setLastName(userInfoRequest.getLastName());
        userInfo.setPhoneNumber(userInfoRequest.getPhoneNumber());
        userInfo.setAddress(userInfoRequest.getAddress());
        userInfo.setCiCard(userInfoRequest.getCiCard());
        try {
            return userInfoRepository.save(userInfo);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create UserInfo", e);
        }
    }

    @Value("${upload.path.root}")
    private String uploadRoot;

    @Value("${upload.path}")
    private String uploadAvatar;

    @Value("${save.path}")
    private String savePath;
    public AvatarData avatarUpdate(Long userInfoId, MultipartFile avatarFile) throws IOException {
        UserInfo userInfo = findUserInfoOrThrow(userInfoId);

        AvatarData checkImage = avatarDataRepository.findByUserInfoId(userInfoId);
        if (checkImage != null) {
            avatarDataRepository.delete(checkImage);
        }

        String type = defindTypeOfImg(avatarFile);
        String fileName = "A000" + userInfoId + "." + type;

        saveImageAvatar(uploadAvatar, avatarFile, fileName);
        saveImageAvatar(uploadRoot, avatarFile, fileName);

        String imgUrl = Paths.get(savePath, "Avatar", fileName).toString();
        AvatarData avatarData = avatarDataRepository.findByUserInfoId(userInfoId);
        if (avatarData != null) {
            avatarData.setImgUrl(imgUrl);
        } else{
            avatarData = new AvatarData(imgUrl,userInfo);
        }
        avatarDataRepository.save(avatarData);

        return avatarData;
    }

    public void saveImageAvatar(String path, MultipartFile imgAvatar, String fileName) throws IOException {
        Path directoryPath = Paths.get(path, "Avatar");
        Path filePath = Paths.get(path, "Avatar", fileName);

        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }

        try (InputStream input = imgAvatar.getInputStream()) {
            // Check if the file already exists
            if (Files.exists(filePath)) {
                // File exists, so we overwrite it
                Files.copy(input, filePath, StandardCopyOption.REPLACE_EXISTING);
            } else {
                // File does not exist, so we simply copy it
                Files.copy(input, filePath);
            }
        }
    }

    public String defindTypeOfImg(MultipartFile file) {
        String contentType = file.getContentType();
        String type = contentType.split("/")[1];
        if (type.equals("jpg"))
            type = "jpeg";
        return type;
    }

    @Value("${upload.path}")
    private String uploadCiCard;
    public List<CICardImage> ciCardImage(Long userInfoId, MultipartFile[] images) throws IOException {
        UserInfo userInfo = findUserInfoOrThrow(userInfoId);
        List<CICardImage> ciCardImages = new ArrayList<>();

        List<CICardImage> checkImages = ciCardRepository.findAllByUserInfoId(userInfoId);
        if (!checkImages.isEmpty()) {
            ciCardRepository.deleteAll(checkImages);
        }

        int index = 1;
        for (MultipartFile image : images) {
            String type = defindTypeOfImg(image);
            String fileName = "C000" + userInfoId + "_" + index + "." + type;

            saveImageCICard(uploadCiCard, image, fileName);
            saveImageCICard(uploadRoot, image, fileName);

            String imgUrl = Paths.get(savePath, "CiCard", fileName).toString();
            CICardImage ciCardImage = new CICardImage(imgUrl, userInfo);
            ciCardImages.add(ciCardImage);
            index++;
        }
        ciCardRepository.saveAll(ciCardImages);

        return ciCardImages;
    }

    public void saveImageCICard(String path, MultipartFile imgCiCard, String fileName) throws IOException {
        Path directoryPath = Paths.get(path, "CiCard");
        Path filePath = Paths.get(path, "CiCard", fileName);

        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }

        try (InputStream input = imgCiCard.getInputStream()) {
            // Check if the file already exists
            if (Files.exists(filePath)) {
                // File exists, so we overwrite it
                Files.copy(input, filePath, StandardCopyOption.REPLACE_EXISTING);
            } else {
                // File does not exist, so we simply copy it
                Files.copy(input, filePath);
            }
        }
    }

    private UserInfo findUserInfoOrThrow(Long id) {
        return userInfoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Not found user info id = " + id));
    }
}
