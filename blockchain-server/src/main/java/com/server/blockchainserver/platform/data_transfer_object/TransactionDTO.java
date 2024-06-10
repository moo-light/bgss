package com.server.blockchainserver.platform.data_transfer_object;

import com.server.blockchainserver.platform.entity.enums.*;

import java.math.BigDecimal;
import java.time.Instant;

public class TransactionDTO {

    private Long id;

    private BigDecimal quantity;

    private BigDecimal pricePerOunce;

    private BigDecimal totalCostOrProfit;

    private TransactionType transactionType;

    private Instant createdAt;

    private Long actionPartyId;

    private GoldUnit goldUnit;
    private String confirmingParty;

    private TransactionVerification transactionVerification;

    private TransactionSignature transactionSignature;
    private TransactionStatus transactionStatus;

    private ContractDTO contract;


    public TransactionDTO() {
    }

    public TransactionDTO(Long id, BigDecimal quantity, BigDecimal pricePerOunce, BigDecimal totalCostOrProfit,
                          TransactionType transactionType, Instant createdAt, Long actionPartyId,
                          GoldUnit goldUnit, String confirmingParty, TransactionVerification transactionVerification,
                          TransactionSignature transactionSignature, TransactionStatus transactionStatus, ContractDTO contract) {
        this.id = id;
        this.quantity = quantity;
        this.pricePerOunce = pricePerOunce;
        this.totalCostOrProfit = totalCostOrProfit;
        this.transactionType = transactionType;
        this.createdAt = createdAt;
        this.actionPartyId = actionPartyId;
        this.goldUnit = goldUnit;
        this.confirmingParty = confirmingParty;
        this.transactionVerification = transactionVerification;
        this.transactionSignature = transactionSignature;
        this.transactionStatus = transactionStatus;
        this.contract = contract;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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


    public Long getActionPartyId() {
        return actionPartyId;
    }

    public void setActionPartyId(Long actionPartyId) {
        this.actionPartyId = actionPartyId;
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

    public ContractDTO getContract() {
        return contract;
    }

    public void setContract(ContractDTO contract) {
        this.contract = contract;
    }

    public TransactionVerification getTransactionVerification() {
        return transactionVerification;
    }

    public void setTransactionVerification(TransactionVerification transactionVerification) {
        this.transactionVerification = transactionVerification;
    }

    public TransactionSignature getTransactionSignature() {
        return transactionSignature;
    }

    public void setTransactionSignature(TransactionSignature transactionSignature) {
        this.transactionSignature = transactionSignature;
    }

    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(TransactionStatus transactionStatus) {
        this.transactionStatus = transactionStatus;
    }
}
