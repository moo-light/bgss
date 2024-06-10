package com.server.blockchainserver.dto.gold_inventory_dto;

import com.server.blockchainserver.platform.entity.CancellationMessage;

public class CancellationMessageDTO {

    private Long id;


    private String sender;

    private String receiver;

    private String reason;

    private long withdrawalId;

    public CancellationMessageDTO() {
    }

    public CancellationMessageDTO(Long id, String sender, String receiver, String reason, long withdrawalId) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.reason = reason;
        this.withdrawalId = withdrawalId;
    }

    public CancellationMessageDTO(Long id) {
        this.id = id;
    }

    public CancellationMessageDTO(CancellationMessage cancellationMessage) {
        this.id = cancellationMessage.getId();
        this.sender = cancellationMessage.getSender();
        this.receiver = cancellationMessage.getReceiver();
        this.reason = cancellationMessage.getReason();
        this.withdrawalId = cancellationMessage.getWithdrawal().getId();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public long getWithdrawalId() {
        return withdrawalId;
    }

    public void setWithdrawalId(long withdrawalId) {
        this.withdrawalId = withdrawalId;
    }
}
