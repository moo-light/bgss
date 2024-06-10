package com.server.blockchainserver.platform.repositories;

import com.server.blockchainserver.platform.entity.WithdrawGold;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WithdrawGoldRepository extends JpaRepository<WithdrawGold, Long> {

    List<WithdrawGold> findByUserInfoId(Long userInfoId, Sort sort);
    List<WithdrawGold> findWithdrawGoldByWithdrawQrCode(String withdrawQrCode);
    @Query("SELECT w FROM WithdrawGold w WHERE w.withdrawQrCode = ?1")
    Optional<WithdrawGold> findByQrCode(String qrcode);
}
