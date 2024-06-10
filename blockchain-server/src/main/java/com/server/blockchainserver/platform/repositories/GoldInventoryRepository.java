package com.server.blockchainserver.platform.repositories;

import com.server.blockchainserver.platform.entity.GoldInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoldInventoryRepository extends JpaRepository<GoldInventory, Long> {
}
