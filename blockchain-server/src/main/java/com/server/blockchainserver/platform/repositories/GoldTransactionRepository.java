package com.server.blockchainserver.platform.repositories;

import com.server.blockchainserver.platform.entity.GoldTransaction;
import com.server.blockchainserver.platform.entity.enums.TransactionStatus;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GoldTransactionRepository extends JpaRepository<GoldTransaction, Long> {
    List<GoldTransaction> findByActionPartyId(Long actionPartyId, Sort sort);

    List<GoldTransaction> findByTransactionStatusAndCreatedAtBefore(TransactionStatus status, Instant cutoff);

//    List<GoldTransaction> findUnverifiedTransactionsOlderThan(LocalDateTime oneDayAgo);
}
