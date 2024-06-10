package com.server.blockchainserver.platform.platform_services.transactionservice;

import com.server.blockchainserver.platform.entity.GoldInventory;
import com.server.blockchainserver.platform.entity.enums.GoldUnit;
import com.server.blockchainserver.platform.platform_services.services_interface.InventoryService;
import com.server.blockchainserver.platform.server.InventoryManagement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private InventoryManagement inventoryManagement;

    @Override
    public GoldInventory addGoldToInventory(BigDecimal quantityInOz, GoldUnit goldUnit) {
        return inventoryManagement.addGoldToInventory(quantityInOz, goldUnit);
    }

    @Override
    public List<GoldInventory> goldInventory() {
        return inventoryManagement.goldInventory();
    }


}
