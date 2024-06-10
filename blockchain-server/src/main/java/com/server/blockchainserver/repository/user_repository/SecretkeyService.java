package com.server.blockchainserver.repository.user_repository;


import com.server.blockchainserver.platform.entity.SecretKey;

public interface SecretkeyService {

    SecretKey createNewSecretKeyForUser(Long userId);


}
