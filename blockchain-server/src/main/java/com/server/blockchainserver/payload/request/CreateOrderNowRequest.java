package com.server.blockchainserver.payload.request;

public class CreateOrderNowRequest {
    private Long productId;
    private Long quantity;
    private String discountCode;
    private boolean isConsignment;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public String getDiscountCode() {
        return discountCode;
    }

    public void setDiscountCode(String discountCode) {
        this.discountCode = discountCode;
    }

    public boolean getIsConsignment() {
        return isConsignment;
    }

    public void setConsignment(boolean consignment) {
        isConsignment = consignment;
    }
}
