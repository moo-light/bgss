package com.server.blockchainserver.repository.user_repository;

import com.server.blockchainserver.models.user_model.AvatarData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvatarDataRepository extends JpaRepository<AvatarData, Long> {

    AvatarData findByUserInfoId(Long userInfoId);

}
