package com.server.blockchainserver.dto.product_dto;

import com.server.blockchainserver.models.enums.EGoldOptionType;
import com.server.blockchainserver.models.shopping_model.ProductCategory;
import com.server.blockchainserver.models.shopping_model.TypeGold;

import java.math.BigDecimal;
import java.util.List;

public class ProductDTOs {
    private Long id;
    private String productName;
    private String description;
    private Long unitOfStock;
    private List<ProductImagesDTO> productImages;
    private ProductCategory category;
    private TypeGold typeGold;
    private Double avgReview = 0D;
    private boolean active;
    private EGoldOptionType typeGoldOption;

    private BigDecimal priceProduct;
    public ProductDTOs() {
    }

    public ProductDTOs(Long id, String productName, String description, Long unitOfStock, List<ProductImagesDTO> productImages,
                       ProductCategory category, TypeGold typeGold, Double avgReview,EGoldOptionType typeGoldOption,
                       boolean active, BigDecimal priceProduct) {
        this.id = id;
        this.productName = productName;
        this.description = description;
        this.unitOfStock = unitOfStock;
        this.productImages = productImages;
        this.category = category;
        this.typeGold = typeGold;
        this.avgReview = avgReview;
        this.typeGoldOption = typeGoldOption;
        this.active = active;
        this.priceProduct =priceProduct;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Long getUnitOfStock() {
        return unitOfStock;
    }

    public void setUnitOfStock(Long unitOfStock) {
        this.unitOfStock = unitOfStock;
    }

    public List<ProductImagesDTO> getProductImages() {
        return productImages;
    }

    public void setProductImages(List<ProductImagesDTO> productImages) {
        this.productImages = productImages;
    }

    public ProductCategory getCategory() {
        return category;
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
    }

    public TypeGold getTypeGold() {
        return typeGold;
    }

    public void setTypeGold(TypeGold typeGold) {
        this.typeGold = typeGold;
    }

    public Double getAvgReview() {
        return avgReview;
    }

    public void setAvgReview(Double avgReview) {
        this.avgReview = avgReview;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public EGoldOptionType getTypeGoldOption() {
        return typeGoldOption;
    }

    public void setTypeGoldOption(EGoldOptionType typeGoldOption) {
        this.typeGoldOption = typeGoldOption;
    }

    public BigDecimal getPriceProduct() {
        return priceProduct;
    }

    public void setPriceProduct(BigDecimal priceProduct) {
        this.priceProduct = priceProduct;
    }
}
