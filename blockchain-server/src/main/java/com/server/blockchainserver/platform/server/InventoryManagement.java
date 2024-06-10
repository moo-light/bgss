package com.server.blockchainserver.platform.server;

import com.server.blockchainserver.platform.entity.GoldInventory;
import com.server.blockchainserver.platform.entity.enums.GoldUnit;
import com.server.blockchainserver.platform.repositories.GoldInventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class InventoryManagement {

    @Autowired
    private GoldInventoryRepository goldInventoryRepository;


    public GoldInventory addGoldToInventory(BigDecimal quantityInOz, GoldUnit goldUnit) {
        List<GoldInventory> inventoryList = goldInventoryRepository.findAll();
        GoldInventory goldInventory;
        BigDecimal convertByGoldUnit = goldUnit.convertToTroyOunce(quantityInOz);
        if (!inventoryList.isEmpty()) {
            goldInventory = inventoryList.get(0);
        } else {
            goldInventory = new GoldInventory();
            goldInventory.setTotalWeightKg(convertByGoldUnit);

        }
//        BigDecimal kilogramsToAdd = convertOuncesToKilograms(quantityInOz);
        goldInventory.setTotalWeightKg(goldInventory.getTotalWeightKg().add(convertByGoldUnit));
        // Lưu thay đổi vào cơ sở dữ liệu
        return goldInventoryRepository.save(goldInventory);

    }

    public List<GoldInventory> goldInventory() {
        List<GoldInventory> goldInventory = goldInventoryRepository.findAll();
        return goldInventory;
    }
}
