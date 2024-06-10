package com.server.blockchainserver.repository.user_repository;

import com.server.blockchainserver.models.token_model.RefreshToken;
import com.server.blockchainserver.models.user_model.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    @Modifying
    void deleteByUser(User user);

    Optional<RefreshToken> findAllByUserUsername(String username);
}
