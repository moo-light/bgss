package com.server.blockchainserver.dto.gold_inventory_dto;

import java.math.BigDecimal;

public class GoldInventoryDTO {

    private long id;
    private BigDecimal totalWeightOz;
    private long userInfoId;

    public GoldInventoryDTO(long id, BigDecimal totalWeightOz, long userInfoId) {
        this.id = id;
        this.totalWeightOz = totalWeightOz;
        this.userInfoId = userInfoId;
    }

    public GoldInventoryDTO() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BigDecimal getTotalWeightOz() {
        return totalWeightOz;
    }

    public void setTotalWeightOz(BigDecimal totalWeightOz) {
        this.totalWeightOz = totalWeightOz;
    }

    public long getUserInfoId() {
        return userInfoId;
    }

    public void setUserInfoId(long userInfoId) {
        this.userInfoId = userInfoId;
    }
}
