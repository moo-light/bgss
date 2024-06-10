package com.server.blockchainserver.platform.server;

import com.server.blockchainserver.platform.entity.GoldTransaction;
import com.server.blockchainserver.platform.entity.enums.TransactionStatus;
import com.server.blockchainserver.platform.repositories.GoldTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class TransactionCleanup {

    @Autowired
    private GoldTransactionRepository goldTransactionRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    private void deletePendingTransactions() {
        Instant cutoff = Instant.now().minus(24, ChronoUnit.HOURS);
        List<GoldTransaction> goldTransactionsToDelete =
                goldTransactionRepository.findByTransactionStatusAndCreatedAtBefore(TransactionStatus.PENDING, cutoff);
        goldTransactionRepository.deleteAll(goldTransactionsToDelete);
    }

}
