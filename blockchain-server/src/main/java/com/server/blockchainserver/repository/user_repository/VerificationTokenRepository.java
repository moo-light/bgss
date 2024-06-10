package com.server.blockchainserver.repository.user_repository;

import com.server.blockchainserver.models.user_model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    VerificationToken findByToken(String token);

    VerificationToken findByUserId(Long userId);
}
