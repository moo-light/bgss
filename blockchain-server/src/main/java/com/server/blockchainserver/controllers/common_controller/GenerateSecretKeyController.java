package com.server.blockchainserver.controllers.common_controller;

import com.server.blockchainserver.platform.data_transfer_object.SecretKeyDTO;
import com.server.blockchainserver.platform.entity.SecretKey;
import com.server.blockchainserver.platform.server.KeyGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class GenerateSecretKeyController {

    @Autowired
    private KeyGenerator keyGenerator;

    /**
     * Generates a new secret key for a given user identified by their userInfoId.
     * This method creates a new secret key using the KeyGenerator service and returns the public key associated with it.
     *
     * @param userInfoId The unique identifier of the user for whom the secret key is to be generated.
     * @return The public key of the newly generated secret key as a String.
     * @throws NoSuchAlgorithmException If the algorithm used for key generation is not available.
     */
    @PostMapping("/generate-secretkey/{userInfoId}")
    public String generateSecretKey(@PathVariable Long userInfoId) throws NoSuchAlgorithmException {
        SecretKey secretKey =  keyGenerator.createNewSecretKeyForUser(userInfoId);
        return secretKey.getPublicKey();
    }

    /**
     * Regenerates a secret key for a given user identified by their userInfoId.
     * This method invokes the KeyGenerator service to create a new secret key, replacing any existing key for the user.
     * It returns the public key associated with the newly generated secret key.
     *
     * @param userInfoId The unique identifier of the user for whom the secret key is to be regenerated.
     * @return The public key of the newly generated secret key as a String.
     * @throws NoSuchAlgorithmException If the algorithm used for key generation is not available.
     */
    @PostMapping("/regenerate-secretkey/{userInfoId}")
    public String regenerateSecretKey(@PathVariable Long userInfoId) throws NoSuchAlgorithmException {
        SecretKey secretKey =  keyGenerator.createNewSecretKeyForUser(userInfoId);
        return secretKey.getPublicKey();
    }

    @PostMapping("/show-secret-key/{userInfoId}")
    public ResponseEntity<SecretKeyDTO> showSecretKey(@PathVariable Long userInfoId) throws NoSuchAlgorithmException {
        SecretKeyDTO secretKey =  keyGenerator.getSecretKey(userInfoId);
        return ResponseEntity.ok(secretKey);
    }


}
