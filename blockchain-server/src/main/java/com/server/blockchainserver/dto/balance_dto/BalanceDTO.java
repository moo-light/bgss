package com.server.blockchainserver.dto.balance_dto;

import java.math.BigDecimal;

public class BalanceDTO {

    private long id;
    private BigDecimal amount;

    private Long userInfoId;

    public BalanceDTO() {
    }


    public long getId() {
        return id;
    }

    public BalanceDTO(long id, BigDecimal amount, Long userInfoId) {
        this.id = id;
        this.amount = amount;
        this.userInfoId = userInfoId;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Long getUserInfoId() {
        return userInfoId;
    }

    public void setUserInfoId(Long userInfoId) {
        this.userInfoId = userInfoId;
    }
}
