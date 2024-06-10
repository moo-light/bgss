package com.server.blockchainserver.repository;

import com.server.blockchainserver.models.AnalyzeBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnalyzeBalanceRepository extends JpaRepository<AnalyzeBalance, Long> {
}
