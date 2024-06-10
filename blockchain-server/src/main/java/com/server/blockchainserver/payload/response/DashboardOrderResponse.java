package com.server.blockchainserver.payload.response;

import java.math.BigDecimal;

public class DashboardOrderResponse {

    private int totalOrders;
    private int unverifiedOrder;
    private int not_receivedOrder;
    private int receivedOrder;
    private BigDecimal totalAmountOrdersPaid;

    public DashboardOrderResponse() {
    }

    public DashboardOrderResponse(int totalOrders, int unverifiedOrder, int not_receivedOrder, int receivedOrder, BigDecimal totalAmountOrdersPaid) {
        this.totalOrders = totalOrders;
        this.unverifiedOrder = unverifiedOrder;
        this.not_receivedOrder = not_receivedOrder;
        this.receivedOrder = receivedOrder;
        this.totalAmountOrdersPaid = totalAmountOrdersPaid;
    }

    public int getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(int totalOrders) {
        this.totalOrders = totalOrders;
    }

    public int getUnverifiedOrder() {
        return unverifiedOrder;
    }

    public void setUnverifiedOrder(int unverifiedOrder) {
        this.unverifiedOrder = unverifiedOrder;
    }

    public int getNot_receivedOrder() {
        return not_receivedOrder;
    }

    public void setNot_receivedOrder(int not_receivedOrder) {
        this.not_receivedOrder = not_receivedOrder;
    }

    public int getReceivedOrder() {
        return receivedOrder;
    }

    public void setReceivedOrder(int receivedOrder) {
        this.receivedOrder = receivedOrder;
    }

    public BigDecimal getTotalAmountOrdersPaid() {
        return totalAmountOrdersPaid;
    }

    public void setTotalAmountOrdersPaid(BigDecimal totalAmountOrdersPaid) {
        this.totalAmountOrdersPaid = totalAmountOrdersPaid;
    }
}
