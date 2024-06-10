package com.server.blockchainserver.platform.data_transfer_object;

import com.server.blockchainserver.dto.gold_inventory_dto.CancellationMessageDTO;
import com.server.blockchainserver.dto.product_dto.ProductDTO;
import com.server.blockchainserver.dto.product_dto.ProductDTOs;
import com.server.blockchainserver.platform.entity.WithdrawGold;
import com.server.blockchainserver.platform.entity.enums.EUserConfirmWithdraw;
import com.server.blockchainserver.platform.entity.enums.GoldUnit;
import com.server.blockchainserver.platform.entity.enums.WithdrawalStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

public class WithdrawGoldDTO {

    private Long id;
    private String withdrawQrCode;
    private Double amount;
    private WithdrawalStatus status;
    private Instant transactionDate;
    private Long userInfoId;
    private Instant updateDate;
    private GoldUnit goldUnit;
    private EUserConfirmWithdraw userConfirm;
    private Set<CancellationMessageDTO> cancellationMessages;
    private ProductDTOs productDTO;

    public WithdrawGoldDTO(Long id, String withdrawQrCode, Double amount, WithdrawalStatus status,
                           Instant transactionDate, Long userInfoId, Instant updateDate, GoldUnit goldUnit,
                           EUserConfirmWithdraw userConfirm, Set<CancellationMessageDTO> cancellationMessages,
                           ProductDTOs productDTO) {

        this.id = id;
        this.withdrawQrCode = withdrawQrCode;
        this.amount = amount;
        this.status = status;
        this.transactionDate = transactionDate;
        this.userInfoId = userInfoId;
        this.updateDate = updateDate;
        this.goldUnit = goldUnit;
        this.userConfirm = userConfirm;
        this.cancellationMessages = cancellationMessages;
        this.productDTO = productDTO;
    }

    public WithdrawGoldDTO(WithdrawGold withdrawGold) {
        this.id = withdrawGold.getId();
        this.withdrawQrCode = withdrawGold.getWithdrawQrCode();
        this.amount = withdrawGold.getAmount();
        this.status = withdrawGold.getStatus();
        this.transactionDate = withdrawGold.getTransactionDate();
        this.userInfoId = withdrawGold.getUserInfo().getId();
        this.updateDate = withdrawGold.getUpdateDate();
        this.goldUnit = withdrawGold.getGoldUnit();
        this.userConfirm = withdrawGold.getUserConfirm();
        this.cancellationMessages = withdrawGold.getCancellationMessages()
                .stream().map(CancellationMessageDTO::new)
                .collect(Collectors.toSet());
    }

    public WithdrawGoldDTO() {
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

//    public Set<CancellationMessage> getCancellationMessages() {
//        return cancellationMessages;
//    }
//
//    public void setCancellationMessages(Set<CancellationMessage> cancellationMessages) {
//        this.cancellationMessages = cancellationMessages;
//    }

    public Long getUserInfoId() {
        return userInfoId;
    }

    public void setUserInfoId(Long userInfoId) {
        this.userInfoId = userInfoId;
    }

    public Instant getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Instant updateDate) {
        this.updateDate = updateDate;
    }

    public Set<CancellationMessageDTO> getCancellationMessages() {
        return cancellationMessages;
    }

    public void setCancellationMessages(Set<CancellationMessageDTO> cancellationMessages) {
        this.cancellationMessages = cancellationMessages;
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
}
