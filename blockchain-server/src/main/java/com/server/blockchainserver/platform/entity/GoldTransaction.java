package com.server.blockchainserver.platform.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.server.blockchainserver.models.user_model.UserInfo;
import com.server.blockchainserver.platform.entity.enums.*;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;


@Entity
@Table(name = "gold_transactions")
public class GoldTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "action_party_id")
    private UserInfo actionParty; // Mỗi giao dịch thuộc về một người dùng

    // Tổng số lượng vàng được mua hoặc bán (tính bằng troy oz)
    @Column(nullable = false, precision = 10, scale = 4)
    private BigDecimal quantity;

    // Giá mỗi troy oz tại thời điểm giao dịch
    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal pricePerOunce;

    // Cost or profit of the transaction
    @Column(precision = 18, scale = 2)
    private BigDecimal totalCostOrProfit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;

    // Thời điểm giao dịch được tạo
    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private String confirmingParty;

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private GoldUnit goldUnit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionVerification transactionVerification = TransactionVerification.UNVERIFIED;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionSignature transactionSignature;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionStatus transactionStatus;

    @OneToOne(mappedBy = "transaction", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    private Contract contract;


    public GoldTransaction(UserInfo actionParty, BigDecimal quantity, BigDecimal pricePerOunce,
                           BigDecimal totalCostOrProfit, TransactionType transactionType, Instant createdAt,
                           String confirmingParty, GoldUnit goldUnit, TransactionVerification transactionVerification,
                           TransactionSignature transactionSignature, TransactionStatus transactionStatus,
                           Contract contract) {
        this.actionParty = actionParty;
        this.quantity = quantity;
        this.pricePerOunce = pricePerOunce;
        this.totalCostOrProfit = totalCostOrProfit;
        this.transactionType = transactionType;
        this.createdAt = createdAt;
        this.confirmingParty = confirmingParty;
        this.goldUnit = goldUnit;
        this.transactionVerification = transactionVerification;
        this.transactionSignature = transactionSignature;
        this.transactionStatus = transactionStatus;
        this.contract = contract;
    }

    public GoldTransaction() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserInfo getActionParty() {
        return actionParty;
    }

    public void setActionParty(UserInfo actionParty) {
        this.actionParty = actionParty;
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

    public BigDecimal setTotalCostOrProfit(BigDecimal totalCostOrProfit) {
        this.totalCostOrProfit = totalCostOrProfit;
        return totalCostOrProfit;
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

    public Instant setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
        return createdAt;
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

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(TransactionStatus transactionStatus) {
        this.transactionStatus = transactionStatus;
    }
}