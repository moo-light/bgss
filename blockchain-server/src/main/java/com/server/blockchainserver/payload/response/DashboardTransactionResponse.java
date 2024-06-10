package com.server.blockchainserver.payload.response;

import java.math.BigDecimal;

public class DashboardTransactionResponse {
    private int totalTransactions;
    private int totalTransactionsBuy;
    private int totalTransactionsSell;
    private int unverifiedTransaction;
    private int verifiedTransaction;
    private int pendingTransaction;
    private int confirmedTransaction;
    private int rejectedTransaction;
    private int completedTransaction;
    private BigDecimal totalAmountTransactionsPaid;

    public DashboardTransactionResponse() {
    }

    public DashboardTransactionResponse(int totalTransactions, int totalTransactionsBuy, int totalTransactionsSell,
                                        int unverifiedTransaction, int verifiedTransaction, int pendingTransaction,
                                        int confirmedTransaction, int rejectedTransaction, int completedTransaction,
                                        BigDecimal totalAmountTransactionsPaid) {
        this.totalTransactions = totalTransactions;
        this.totalTransactionsBuy = totalTransactionsBuy;
        this.totalTransactionsSell = totalTransactionsSell;
        this.unverifiedTransaction = unverifiedTransaction;
        this.verifiedTransaction = verifiedTransaction;
        this.pendingTransaction = pendingTransaction;
        this.confirmedTransaction = confirmedTransaction;
        this.rejectedTransaction = rejectedTransaction;
        this.completedTransaction = completedTransaction;
        this.totalAmountTransactionsPaid = totalAmountTransactionsPaid;
    }

    public int getTotalTransactions() {
        return totalTransactions;
    }

    public void setTotalTransactions(int totalTransactions) {
        this.totalTransactions = totalTransactions;
    }

    public int getTotalTransactionsBuy() {
        return totalTransactionsBuy;
    }

    public void setTotalTransactionsBuy(int totalTransactionsBuy) {
        this.totalTransactionsBuy = totalTransactionsBuy;
    }

    public int getTotalTransactionsSell() {
        return totalTransactionsSell;
    }

    public void setTotalTransactionsSell(int totalTransactionsSell) {
        this.totalTransactionsSell = totalTransactionsSell;
    }

    public int getUnverifiedTransaction() {
        return unverifiedTransaction;
    }

    public void setUnverifiedTransaction(int unverifiedTransaction) {
        this.unverifiedTransaction = unverifiedTransaction;
    }

    public int getVerifiedTransaction() {
        return verifiedTransaction;
    }

    public void setVerifiedTransaction(int verifiedTransaction) {
        this.verifiedTransaction = verifiedTransaction;
    }

    public int getPendingTransaction() {
        return pendingTransaction;
    }

    public void setPendingTransaction(int pendingTransaction) {
        this.pendingTransaction = pendingTransaction;
    }

    public int getConfirmedTransaction() {
        return confirmedTransaction;
    }

    public void setConfirmedTransaction(int confirmedTransaction) {
        this.confirmedTransaction = confirmedTransaction;
    }

    public int getRejectedTransaction() {
        return rejectedTransaction;
    }

    public void setRejectedTransaction(int rejectedTransaction) {
        this.rejectedTransaction = rejectedTransaction;
    }

    public int getCompletedTransaction() {
        return completedTransaction;
    }

    public void setCompletedTransaction(int completedTransaction) {
        this.completedTransaction = completedTransaction;
    }

    public BigDecimal getTotalAmountTransactionsPaid() {
        return totalAmountTransactionsPaid;
    }

    public void setTotalAmountTransactionsPaid(BigDecimal totalAmountTransactionsPaid) {
        this.totalAmountTransactionsPaid = totalAmountTransactionsPaid;
    }
}
