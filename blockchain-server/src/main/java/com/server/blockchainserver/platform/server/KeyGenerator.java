package com.server.blockchainserver.platform.server;

import com.server.blockchainserver.exeptions.NotFoundException;
import com.server.blockchainserver.models.user_model.UserInfo;
import com.server.blockchainserver.platform.data_transfer_object.SecretKeyDTO;
import com.server.blockchainserver.platform.entity.SecretKey;
import com.server.blockchainserver.repository.user_repository.SecretKeyRepository;
import com.server.blockchainserver.repository.user_repository.UserInfoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.*;
import java.util.Base64;
import java.util.Optional;

@Component
public class KeyGenerator {

    @Autowired
    private SecretKeyRepository secretKeyRepository;
    @Autowired
    private UserInfoRepository userInfoRepository;

    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(2048);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        return keyPair;
    }

    public static String publicKeyToBase64(PublicKey publicKey) {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    public static String privateKeyToBase64(PrivateKey privateKey) {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    @Transactional
    public SecretKey createNewSecretKeyForUser(Long userInfoId) throws NoSuchAlgorithmException {
        UserInfo userInfo = userInfoRepository.findById(userInfoId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userInfoId));
        KeyPair keyPair = KeyGenerator.generateKeyPair();
        String publicKeyBase64 = KeyGenerator.publicKeyToBase64(keyPair.getPublic());
        String privateKeyBase64 = KeyGenerator.privateKeyToBase64(keyPair.getPrivate());

        Optional<SecretKey> secretKeyOpt = secretKeyRepository.findByUserInfo(userInfo);
        if (secretKeyOpt.isPresent()) {
            throw new NoSuchAlgorithmException("Secret key already available for this user.");
        }

        SecretKey secretKey = new SecretKey();
        secretKey.setPublicKey(publicKeyBase64);
        secretKey.setPrivateKey(privateKeyBase64);
        secretKey.setUserInfo(userInfo);

        secretKeyRepository.save(secretKey);

        userInfo.setSecretKey(secretKey);

        userInfoRepository.save(userInfo);

        return secretKey;
    }

    @Transactional
    public SecretKeyDTO getSecretKey(Long userInfoId) {
        UserInfo userInfo = userInfoRepository.findById(userInfoId)
                .orElseThrow(() -> new NotFoundException("User info not found for user " + userInfoId));
        SecretKey secretKey = secretKeyRepository.findByUserInfo(userInfo)
                .orElseThrow(() -> new NotFoundException("Secret key not found for user " + userInfoId));
        SecretKeyDTO secretKeyDTO = new SecretKeyDTO();
        secretKeyDTO.setId(secretKey.getId());
        secretKeyDTO.setPublicKey(secretKey.getPublicKey());
        secretKeyDTO.setPrivateKey(secretKey.getPrivateKey());
        secretKeyDTO.setUserInfoId(userInfo.getId());
        return secretKeyDTO;
    }

}
