package com.server.blockchainserver.repository.user_repository;

import com.server.blockchainserver.models.user_model.UserInfo;
import com.server.blockchainserver.platform.entity.SecretKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SecretKeyRepository extends JpaRepository<SecretKey, Long> {
    Optional<SecretKey> findByUserInfo(UserInfo userInfo);

    SecretKey findByPublicKey(String publicKey);
}
