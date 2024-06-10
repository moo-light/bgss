package com.server.blockchainserver.payload.response;

import java.math.BigDecimal;

public class DashboardWithdrawResponse {
    private int totalRequestWithdraws;
    private int unverifiedWithdraw;
    private int pendingWithdraw;
    private int confirmedWithdraw;
    private int completedWithdraw;
    private int canceledWithdraw;
    private BigDecimal amountWithdrawGoldConfirmed;
    private BigDecimal totalAmountWithdrawGold;

    public DashboardWithdrawResponse() {
    }

    public DashboardWithdrawResponse(int totalRequestWithdraws, int unverifiedWithdraw, int pendingWithdraw,
                                     int confirmedWithdraw, int completedWithdraw, int canceledWithdraw,
                                     BigDecimal amountWithdrawGoldConfirmed, BigDecimal totalAmountWithdrawGold) {
        this.totalRequestWithdraws = totalRequestWithdraws;
        this.unverifiedWithdraw = unverifiedWithdraw;
        this.pendingWithdraw = pendingWithdraw;
        this.confirmedWithdraw = confirmedWithdraw;
        this.completedWithdraw = completedWithdraw;
        this.canceledWithdraw = canceledWithdraw;
        this.amountWithdrawGoldConfirmed = amountWithdrawGoldConfirmed;
        this.totalAmountWithdrawGold = totalAmountWithdrawGold;
    }

    public int getTotalRequestWithdraws() {
        return totalRequestWithdraws;
    }

    public void setTotalRequestWithdraws(int totalRequestWithdraws) {
        this.totalRequestWithdraws = totalRequestWithdraws;
    }

    public int getUnverifiedWithdraw() {
        return unverifiedWithdraw;
    }

    public void setUnverifiedWithdraw(int unverifiedWithdraw) {
        this.unverifiedWithdraw = unverifiedWithdraw;
    }

    public int getPendingWithdraw() {
        return pendingWithdraw;
    }

    public void setPendingWithdraw(int pendingWithdraw) {
        this.pendingWithdraw = pendingWithdraw;
    }

    public int getConfirmedWithdraw() {
        return confirmedWithdraw;
    }

    public void setConfirmedWithdraw(int confirmedWithdraw) {
        this.confirmedWithdraw = confirmedWithdraw;
    }

    public int getCompletedWithdraw() {
        return completedWithdraw;
    }

    public void setCompletedWithdraw(int completedWithdraw) {
        this.completedWithdraw = completedWithdraw;
    }

    public int getCanceledWithdraw() {
        return canceledWithdraw;
    }

    public void setCanceledWithdraw(int canceledWithdraw) {
        this.canceledWithdraw = canceledWithdraw;
    }

    public BigDecimal getAmountWithdrawGoldConfirmed() {
        return amountWithdrawGoldConfirmed;
    }

    public void setAmountWithdrawGoldConfirmed(BigDecimal amountWithdrawGoldConfirmed) {
        this.amountWithdrawGoldConfirmed = amountWithdrawGoldConfirmed;
    }

    public BigDecimal getTotalAmountWithdrawGold() {
        return totalAmountWithdrawGold;
    }

    public void setTotalAmountWithdrawGold(BigDecimal totalAmountWithdrawGold) {
        this.totalAmountWithdrawGold = totalAmountWithdrawGold;
    }
}
