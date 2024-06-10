package com.server.blockchainserver.payload.request.shopping_request;

import com.server.blockchainserver.models.enums.EGoldOptionType;
import jakarta.annotation.Nullable;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

public class ProductRequest {
    private String productName;
    private Double weight;
    private String description;
    @Nullable
    private BigDecimal processingCost;
    //ONLY USE THIS FIELD FOR UPDATE PRODUCT
    @Nullable
    private Double percentageReduce;
    private Long totalUnitOfStock;
    private Long categoryId;
    private Long typeGoldId;
    private EGoldOptionType typeOption;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Nullable
    public BigDecimal getProcessingCost() {
        return processingCost;
    }

    public void setProcessingCost(@Nullable BigDecimal processingCost) {
        this.processingCost = processingCost;
    }

    public Long getTotalUnitOfStock() {
        return totalUnitOfStock;
    }

    public void setTotalUnitOfStock(Long totalUnitOfStock) {
        this.totalUnitOfStock = totalUnitOfStock;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    @Nullable
    public Double getPercentageReduce() {
        return percentageReduce;
    }

    public void setPercentageReduce(@Nullable Double percentageReduce) {
        this.percentageReduce = percentageReduce;
    }

    public Long getTypeGoldId() {
        return typeGoldId;
    }

    public void setTypeGoldId(Long typeGoldId) {
        this.typeGoldId = typeGoldId;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public EGoldOptionType getTypeOption() {
        return typeOption;
    }

    public void setTypeOption(EGoldOptionType typeOption) {
        this.typeOption = typeOption;
    }
}

