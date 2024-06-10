package com.server.blockchainserver.payload.request.shopping_request;

import java.util.List;

public class CreateOrderRequest {
    private List<Long> listCartId;

    private String discountCode;
    private boolean isConsignment;

    public List<Long> getListCartId() {
        return listCartId;
    }

    public void setListCartId(List<Long> listCartId) {
        this.listCartId = listCartId;
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
