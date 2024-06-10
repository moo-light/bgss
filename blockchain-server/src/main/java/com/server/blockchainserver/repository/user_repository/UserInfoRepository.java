package com.server.blockchainserver.repository.user_repository;

import com.server.blockchainserver.models.user_model.UserInfo;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {

    @Nonnull
    Optional<UserInfo> findById(@Nonnull Long id);

    UserInfo findByUserId(Long id);

    UserInfo findUserInfoByUserId(Long id);
}
