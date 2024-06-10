package com.server.blockchainserver.platform.data_transfer_object;

import com.server.blockchainserver.platform.entity.UserGoldInventory;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class UserGoldInventoryDTO {

    private Long id;
    private BigDecimal totalWeightOz;

    private List<WithdrawGoldDTO> withdrawGold;

    public UserGoldInventoryDTO(UserGoldInventory inventory) {
        this.id = inventory.getId();
        this.totalWeightOz = inventory.getTotalWeightOz();
        this.withdrawGold = inventory.getWithdrawGold().stream()
                .map(WithdrawGoldDTO::new)
                .collect(Collectors.toList());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getTotalWeightOz() {
        return totalWeightOz;
    }

    public void setTotalWeightOz(BigDecimal totalWeightOz) {
        this.totalWeightOz = totalWeightOz;
    }

    public List<WithdrawGoldDTO> getWithdrawGold() {
        return withdrawGold;
    }

    public void setWithdrawGold(List<WithdrawGoldDTO> withdrawGold) {
        this.withdrawGold = withdrawGold;
    }
}
