package com.server.blockchainserver.platform.repositories;

import com.server.blockchainserver.platform.entity.CancellationMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CancellationMessageRepository extends JpaRepository<CancellationMessage, Long> {
}
