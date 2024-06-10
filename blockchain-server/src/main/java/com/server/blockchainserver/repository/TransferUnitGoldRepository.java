package com.server.blockchainserver.repository;

import com.server.blockchainserver.models.TransferUnitGold;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferUnitGoldRepository extends JpaRepository<TransferUnitGold, Long> {
}
