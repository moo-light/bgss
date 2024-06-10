package com.server.blockchainserver.platform.data_transfer_object;

import com.server.blockchainserver.platform.entity.enums.ContractStatus;
import com.server.blockchainserver.platform.entity.enums.GoldUnit;
import com.server.blockchainserver.platform.entity.enums.TransactionType;

import java.math.BigDecimal;
import java.util.Date;

public class ContractDTO {

    private Long id;

    private Long actionParty;

    private String fullName;

    private String address;

    private BigDecimal quantity;

    private BigDecimal pricePerOunce;

    private BigDecimal totalCostOrProfit;

    private TransactionType transactionType;

    private Date createdAt;

    private String confirmingParty;

    private GoldUnit goldUnit;

    private String signatureActionParty;

    private String signatureConfirmingParty;

    private ContractStatus contractStatus;

    public ContractDTO(Long id, Long actionParty, String fullName, String address, BigDecimal quantity,
                       BigDecimal pricePerOunce, BigDecimal totalCostOrProfit, TransactionType transactionType,
                       Date createdAt, String confirmingParty, GoldUnit goldUnit, String signatureActionParty,
                       String signatureConfirmingParty, ContractStatus contractStatus) {
        this.id = id;
        this.actionParty = actionParty;
        this.fullName = fullName;
        this.address = address;
        this.quantity = quantity;
        this.pricePerOunce = pricePerOunce;
        this.totalCostOrProfit = totalCostOrProfit;
        this.transactionType = transactionType;
        this.createdAt = createdAt;
        this.confirmingParty = confirmingParty;
        this.goldUnit = goldUnit;
        this.signatureActionParty = signatureActionParty;
        this.signatureConfirmingParty = signatureConfirmingParty;
        this.contractStatus = contractStatus;
    }

    public ContractDTO() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getActionParty() {
        return actionParty;
    }

    public void setActionParty(Long actionParty) {
        this.actionParty = actionParty;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
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

    public String getSignatureActionParty() {
        return signatureActionParty;
    }

    public void setSignatureActionParty(String signatureActionParty) {
        this.signatureActionParty = signatureActionParty;
    }

    public String getSignatureConfirmingParty() {
        return signatureConfirmingParty;
    }

    public void setSignatureConfirmingParty(String signatureConfirmingParty) {
        this.signatureConfirmingParty = signatureConfirmingParty;
    }

    public ContractStatus getContractStatus() {
        return contractStatus;
    }

    public void setContractStatus(ContractStatus contractStatus) {
        this.contractStatus = contractStatus;
    }
}
