package com.server.blockchainserver.repository;

import com.server.blockchainserver.models.AnalyzeOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnalyzeOrderRepository extends JpaRepository<AnalyzeOrder, Long> {
}
