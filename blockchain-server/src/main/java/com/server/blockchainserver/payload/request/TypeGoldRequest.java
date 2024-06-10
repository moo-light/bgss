package com.server.blockchainserver.payload.request;

import com.server.blockchainserver.platform.entity.enums.GoldUnit;

import java.math.BigDecimal;

public class TypeGoldRequest {
    private String typeName;
    private BigDecimal price;
    private GoldUnit goldUnit;

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public GoldUnit getGoldUnit() {
        return goldUnit;
    }

    public void setGoldUnit(GoldUnit goldUnit) {
        this.goldUnit = goldUnit;
    }
}
