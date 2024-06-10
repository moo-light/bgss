package com.server.blockchainserver.payload.request.shopping_request;

import java.math.BigDecimal;
import java.time.Instant;

public class DiscountCodeRequest {
    private Double discountPercentage;
    private BigDecimal minPrice;
    private BigDecimal maxReduce;
    private int quantityMin;
    private int defaultQuantity;
    private Instant dateExpire;

    public Double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(Double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public Instant getDateExpire() {
        return dateExpire;
    }

    public void setDateExpire(Instant dateExpire) {
        this.dateExpire = dateExpire;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public BigDecimal getMaxReduce() {
        return maxReduce;
    }

    public void setMaxReduce(BigDecimal maxReduce) {
        this.maxReduce = maxReduce;
    }

    public int getQuantityMin() {
        return quantityMin;
    }

    public void setQuantityMin(int quantityMin) {
        this.quantityMin = quantityMin;
    }

    public int getDefaultQuantity() {
        return defaultQuantity;
    }

    public void setDefaultQuantity(int defaultQuantity) {
        this.defaultQuantity = defaultQuantity;
    }
}
