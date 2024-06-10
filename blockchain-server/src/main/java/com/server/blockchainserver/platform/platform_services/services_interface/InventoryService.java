package com.server.blockchainserver.platform.platform_services.services_interface;

import com.server.blockchainserver.platform.entity.GoldInventory;
import com.server.blockchainserver.platform.entity.enums.GoldUnit;

import java.math.BigDecimal;
import java.util.List;

public interface InventoryService {

    GoldInventory addGoldToInventory(BigDecimal quantityInOz, GoldUnit goldUnit);

    List<GoldInventory> goldInventory();
}
