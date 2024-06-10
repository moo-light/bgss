package com.server.blockchainserver.platform.data_transfer_object;


import com.server.blockchainserver.platform.entity.enums.EUserConfirmWithdraw;
import com.server.blockchainserver.dto.product_dto.ProductDTO;
import com.server.blockchainserver.dto.product_dto.ProductDTOs;
import com.server.blockchainserver.platform.entity.enums.GoldUnit;
import com.server.blockchainserver.platform.entity.enums.WithdrawRequirement;
import com.server.blockchainserver.platform.entity.enums.WithdrawalStatus;

import java.math.BigDecimal;
import java.time.Instant;

public class WithdrawGoldDefault {

    private Long id;
    private String withdrawQrCode;
    private Double amount;
    private WithdrawalStatus status;
    private Instant transactionDate;
    private Instant updateDate;
    private Long userId;
    private GoldUnit goldUnit;
    private EUserConfirmWithdraw userConfirm;
    private ProductDTOs productDTO;
    private WithdrawRequirement withdrawRequirement;


    public WithdrawGoldDefault(String withdrawQrCode, Double amount, WithdrawalStatus status, Instant transactionDate,
                               Instant updateDate, Long userId, GoldUnit goldUnit, EUserConfirmWithdraw userConfirm,
                               ProductDTOs productDTO, Long id, WithdrawRequirement withdrawRequirement) {

        this.withdrawQrCode = withdrawQrCode;
        this.amount = amount;
        this.status = status;
        this.transactionDate = transactionDate;
        this.updateDate = updateDate;
        this.userId = userId;
        this.goldUnit = goldUnit;
        this.userConfirm = userConfirm;
        this.productDTO = productDTO;
        this.id = id;
        this.withdrawRequirement = withdrawRequirement;
    }

    public WithdrawGoldDefault() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public ProductDTOs getProductDTO() {
        return productDTO;
    }

    public void setProductDTO(ProductDTOs productDTO) {
        this.productDTO = productDTO;
    }

    public WithdrawRequirement getWithdrawRequirement() {
        return withdrawRequirement;
    }

    public void setWithdrawRequirement(WithdrawRequirement withdrawRequirement) {
        this.withdrawRequirement = withdrawRequirement;
    }
}
