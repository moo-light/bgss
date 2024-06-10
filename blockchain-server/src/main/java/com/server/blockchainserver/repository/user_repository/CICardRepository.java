package com.server.blockchainserver.repository.user_repository;

import com.server.blockchainserver.models.user_model.CICardImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CICardRepository extends JpaRepository<CICardImage, Long> {
    List<CICardImage> findAllByUserInfoId(Long userInfoId);
}
