package com.server.blockchainserver.models.shopping_model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.server.blockchainserver.models.enums.EStatusDiscount;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "discounts")
public class Discount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    private Double percentage;
    private BigDecimal minPrice;
    private BigDecimal maxReduce;
    private int quantityMin;
    private int defaultQuantity;
    private Instant dateCreate;
    private Instant dateExpire;
    private boolean isExpire;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EStatusDiscount statusDiscount;

    public Discount() {
    }

    public Discount(String code, Double percentage, BigDecimal minPrice, BigDecimal maxReduce, int quantityMin, int defaultQuantity,
                    Instant dateCreate, Instant dateExpire, boolean isExpire, EStatusDiscount statusDiscount) {
        this.code = code;
        this.percentage = percentage;
        this.minPrice = minPrice;
        this.maxReduce = maxReduce;
        this.quantityMin = quantityMin;
        this.defaultQuantity = defaultQuantity;
        this.dateCreate = dateCreate;
        this.dateExpire = dateExpire;
        this.isExpire = isExpire;
        this.statusDiscount = statusDiscount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    public boolean isExpire() {
        return isExpire;
    }

    public void setExpire(boolean expire) {
        isExpire = expire;
    }

    public Instant getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(Instant dateCreate) {
        this.dateCreate = dateCreate;
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

    public EStatusDiscount getStatusDiscount() {
        return statusDiscount;
    }

    public void setStatusDiscount(EStatusDiscount statusDiscount) {
        this.statusDiscount = statusDiscount;
    }

    public int getDefaultQuantity() {
        return defaultQuantity;
    }

    public void setDefaultQuantity(int defaultQuantity) {
        this.defaultQuantity = defaultQuantity;
    }
}
