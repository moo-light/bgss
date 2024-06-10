package com.server.blockchainserver.platform.repositories;

import com.server.blockchainserver.models.user_model.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {
    List<PaymentHistory> findAllByUserInfoId(Long userInfoId);

    @Query("SELECT p.orderCode FROM PaymentHistory p WHERE p.userInfo.id = :userInfoId")
    List<String> findOrderCodesByUserInfoId(@Param("userInfoId") Long userInfoId);
}
