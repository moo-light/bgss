package com.server.blockchainserver.platform.repositories;

import com.server.blockchainserver.models.user_model.Balance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BalanceRepository extends JpaRepository<Balance, Long> {
    Optional<Balance> findBalanceByUserInfoId(long userInfoId);

}
