package com.server.blockchainserver.platform.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "cancellation_message")
public class CancellationMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String sender;

    private String receiver;

    private String reason;

    @ManyToOne
    @JoinColumn(name = "withdrawal_id")
    @JsonIgnore// foreign key column trong CancellationMessage table
    private WithdrawGold withdrawal;

    public CancellationMessage() {
    }

    public CancellationMessage(String sender, String receiver, String reason, WithdrawGold withdrawal) {
        this.sender = sender;
        this.receiver = receiver;
        this.reason = reason;
        this.withdrawal = withdrawal;
    }

    public CancellationMessage(CancellationMessage cancellationMessage) {

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

    public WithdrawGold getWithdrawal() {
        return withdrawal;
    }

    public void setWithdrawal(WithdrawGold withdrawal) {
        this.withdrawal = withdrawal;
    }
}
