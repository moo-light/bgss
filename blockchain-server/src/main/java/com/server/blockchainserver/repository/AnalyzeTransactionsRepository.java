package com.server.blockchainserver.repository;

import com.server.blockchainserver.models.AnalyzeTransactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnalyzeTransactionsRepository extends JpaRepository<AnalyzeTransactions, Long> {
}
