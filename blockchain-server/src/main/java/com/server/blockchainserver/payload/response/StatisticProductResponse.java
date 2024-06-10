package com.server.blockchainserver.payload.response;

import com.server.blockchainserver.models.shopping_model.Product;

import java.math.BigDecimal;
import java.util.List;

public class StatisticProductResponse {
    private List<Product> productList;
    private Double totalWeight;
    private Long totalQuantity;
    private BigDecimal totalAmount;

    public StatisticProductResponse() {
    }

    public StatisticProductResponse(List<Product> productList, Double totalWeight, Long totalQuantity, BigDecimal totalAmount) {
        this.productList = productList;
        this.totalWeight = totalWeight;
        this.totalQuantity = totalQuantity;
        this.totalAmount = totalAmount;
    }

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    public Double getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(Double totalWeight) {
        this.totalWeight = totalWeight;
    }

    public Long getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Long totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}
