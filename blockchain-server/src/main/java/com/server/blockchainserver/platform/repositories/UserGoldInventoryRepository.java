package com.server.blockchainserver.platform.repositories;

import com.server.blockchainserver.platform.entity.UserGoldInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserGoldInventoryRepository extends JpaRepository<UserGoldInventory, Long> {
    UserGoldInventory findByUserInfoId(long userId);
}
