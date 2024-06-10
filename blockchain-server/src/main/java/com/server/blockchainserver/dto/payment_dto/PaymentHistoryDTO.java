package com.server.blockchainserver.dto.payment_dto;

import com.server.blockchainserver.models.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;

public class PaymentHistoryDTO {

    private long id;
    private String orderCode;
    private BigDecimal amount;
    private String bankCode;
    private PaymentStatus paymentStatus;
    private Instant payDate;
    private String reason;
    private long userInfoId;

    private BigDecimal balance;


    public PaymentHistoryDTO() {
    }

    public PaymentHistoryDTO(long id, String orderCode, BigDecimal amount, String bankCode,
                             PaymentStatus paymentStatus, Instant payDate, String reason, long userInfoId, BigDecimal balance) {
        this.id = id;
        this.orderCode = orderCode;
        this.amount = amount;
        this.bankCode = bankCode;
        this.paymentStatus = paymentStatus;
        this.payDate = payDate;
        this.reason = reason;
        this.userInfoId = userInfoId;
        this.balance = balance;
    }

    public PaymentHistoryDTO(long id, String orderCode, BigDecimal amount, String bankCode,
                             PaymentStatus paymentStatus, Instant payDate, String reason, long userInfoId) {
        this.id = id;
        this.orderCode = orderCode;
        this.amount = amount;
        this.bankCode = bankCode;
        this.paymentStatus = paymentStatus;
        this.payDate = payDate;
        this.reason = reason;
        this.userInfoId = userInfoId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Instant getPayDate() {
        return payDate;
    }

    public void setPayDate(Instant payDate) {
        this.payDate = payDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public long getUserInfoId() {
        return userInfoId;
    }

    public void setUserInfoId(long userInfoId) {
        this.userInfoId = userInfoId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
