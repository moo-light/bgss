package com.server.blockchainserver.dto.gold_inventory_dto;

import com.server.blockchainserver.platform.entity.GoldTransaction;
import com.server.blockchainserver.platform.entity.enums.GoldUnit;
import com.server.blockchainserver.platform.entity.enums.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;

public class GoldTransactionDTO {
    private long id;
    private BigDecimal quantity;
    private BigDecimal pricePerOunce;
    private BigDecimal totalCostOrProfit;
    private TransactionType transactionType;
    private Instant createdAt;
    private String confirmingParty;
    private GoldUnit goldUnit;
    private long actionParty;

    public GoldTransactionDTO(long id, BigDecimal quantity, BigDecimal pricePerOunce,
                              BigDecimal totalCostOrProfit, TransactionType transactionType,
                              Instant createdAt, String confirmingParty, GoldUnit goldUnit, long actionParty) {
        this.id = id;
        this.quantity = quantity;
        this.pricePerOunce = pricePerOunce;
        this.totalCostOrProfit = totalCostOrProfit;
        this.transactionType = transactionType;
        this.createdAt = createdAt;
        this.confirmingParty = confirmingParty;
        this.goldUnit = goldUnit;
        this.actionParty = actionParty;
    }

    public GoldTransactionDTO(GoldTransaction goldTransaction) {
        this.id = goldTransaction.getId();
        this.quantity = goldTransaction.getQuantity();
        this.pricePerOunce = goldTransaction.getPricePerOunce();
        this.totalCostOrProfit = goldTransaction.getTotalCostOrProfit();
        this.transactionType = goldTransaction.getTransactionType();
        this.createdAt = goldTransaction.getCreatedAt();
        this.goldUnit = goldTransaction.getGoldUnit();
        this.confirmingParty = goldTransaction.getConfirmingParty();
        this.actionParty = goldTransaction.getActionParty().getId();

    }

    public GoldTransactionDTO() {
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPricePerOunce() {
        return pricePerOunce;
    }

    public void setPricePerOunce(BigDecimal pricePerOunce) {
        this.pricePerOunce = pricePerOunce;
    }

    public BigDecimal getTotalCostOrProfit() {
        return totalCostOrProfit;
    }

    public void setTotalCostOrProfit(BigDecimal totalCostOrProfit) {
        this.totalCostOrProfit = totalCostOrProfit;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getConfirmingParty() {
        return confirmingParty;
    }

    public void setConfirmingParty(String confirmingParty) {
        this.confirmingParty = confirmingParty;
    }

    public GoldUnit getGoldUnit() {
        return goldUnit;
    }

    public void setGoldUnit(GoldUnit goldUnit) {
        this.goldUnit = goldUnit;
    }

    public long getActionParty() {
        return actionParty;
    }

    public void setActionParty(long actionParty) {
        this.actionParty = actionParty;
    }
}
