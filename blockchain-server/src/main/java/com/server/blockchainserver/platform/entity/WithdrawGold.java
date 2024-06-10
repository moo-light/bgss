package com.server.blockchainserver.platform.entity;

import com.server.blockchainserver.models.shopping_model.Product;
import com.server.blockchainserver.models.user_model.UserInfo;
import com.server.blockchainserver.platform.entity.enums.EUserConfirmWithdraw;
import com.server.blockchainserver.platform.entity.enums.GoldUnit;
import com.server.blockchainserver.platform.entity.enums.WithdrawRequirement;
import com.server.blockchainserver.platform.entity.enums.WithdrawalStatus;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "withdraw_gold")
public class WithdrawGold {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_info_id", nullable = false)
    private UserInfo userInfo; // Người dùng yêu cầu rút vàng

    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private WithdrawalStatus status; // Trạng thái của yêu cầu

    @Column(name = "transaction_date", nullable = false)
    private Instant transactionDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id", nullable = false)
    private UserGoldInventory inventory;

    @Column(name = "update_date")
    private Instant updateDate;

    @Column(name = "gold_unit")
    private GoldUnit goldUnit;

    @Column(name = "withdraw_qr_code")
    private String withdrawQrCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EUserConfirmWithdraw userConfirm;

    @OneToMany(mappedBy = "withdrawal", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CancellationMessage> cancellationMessages = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "option_requirement",nullable = true)
    private WithdrawRequirement withdrawRequirement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = true)
    private Product product;



    public WithdrawGold() {
    }

//    public WithdrawGold(UserInfo userInfo, BigDecimal amount, WithdrawalStatus status,
//                        Instant transactionDate, UserGoldInventory inventory, Instant updateDate, GoldUnit goldUnit,
//                        String withdrawQrCode, EUserConfirmWithdraw userConfirm ,Set<CancellationMessage> cancellationMessages) {
//        this.userInfo = userInfo;
//        this.amount = amount;
//        this.status = status;
//        this.transactionDate = transactionDate;
//        this.inventory = inventory;
//        this.cancellationMessages = cancellationMessages;
//        this.updateDate = updateDate;
//        this.goldUnit = goldUnit;
//        this.withdrawQrCode = withdrawQrCode;
//        this.userConfirm = userConfirm;
//    }


    public WithdrawGold(UserInfo userInfo, Double amount, WithdrawalStatus status, Instant transactionDate,
                        UserGoldInventory inventory, Instant updateDate, GoldUnit goldUnit, String withdrawQrCode,
                        EUserConfirmWithdraw userConfirm, Set<CancellationMessage> cancellationMessages,
                        WithdrawRequirement withdrawRequirement, Product product) {
        this.userInfo = userInfo;
        this.amount = amount;
        this.status = status;
        this.transactionDate = transactionDate;
        this.inventory = inventory;
        this.updateDate = updateDate;
        this.goldUnit = goldUnit;
        this.withdrawQrCode = withdrawQrCode;
        this.userConfirm = userConfirm;
        this.cancellationMessages = cancellationMessages;
        this.withdrawRequirement = withdrawRequirement;
        this.product = product;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public WithdrawalStatus getStatus() {
        return status;
    }

    public void setStatus(WithdrawalStatus status) {
        this.status = status;
    }

    public Instant getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Instant transactionDate) {
        this.transactionDate = transactionDate;
    }

    public UserGoldInventory getUserInventory() {
        return inventory;
    }

    public void setUserInventory(UserGoldInventory userInventory) {
        this.inventory = userInventory;
    }

    public UserGoldInventory getInventory() {
        return inventory;
    }

    public void setInventory(UserGoldInventory inventory) {
        this.inventory = inventory;
    }

    public Set<CancellationMessage> getCancellationMessages() {
        return cancellationMessages;
    }

    public void setCancellationMessages(Set<CancellationMessage> cancellationMessages) {
        this.cancellationMessages = cancellationMessages;
    }

    public Instant getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Instant updateDate) {
        this.updateDate = updateDate;
    }

    public GoldUnit getGoldUnit() {
        return goldUnit;
    }

    public void setGoldUnit(GoldUnit goldUnit) {
        this.goldUnit = goldUnit;
    }

    public String getWithdrawQrCode() {
        return withdrawQrCode;
    }

    public void setWithdrawQrCode(String withdrawQrCode) {
        this.withdrawQrCode = withdrawQrCode;
    }

    public EUserConfirmWithdraw getUserConfirm() {
        return userConfirm;
    }

    public void setUserConfirm(EUserConfirmWithdraw userConfirm) {
        this.userConfirm = userConfirm;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public WithdrawRequirement getWithdrawRequirement() {
        return withdrawRequirement;
    }

    public void setWithdrawRequirement(WithdrawRequirement withdrawRequirement) {
        this.withdrawRequirement = withdrawRequirement;
    }
}
