package com.server.blockchainserver.models.shopping_model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.server.blockchainserver.models.enums.EGoldOptionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "products")
public class Product implements Comparable<Product> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 50)
    private String productName;

    @NotBlank
    @Column(length = 2000)
    private String description;
    private Double weight;
    @NotNull
    private BigDecimal processingCost;

    private Long totalUnitOfStock;
    @NotNull
    private Long unitOfStock;
    private Long totalProductSold;
    private Double percentageReduce;
    private BigDecimal secondPrice;

    @ManyToOne
    @JoinColumn(name = "categories_id")
    private ProductCategory category;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ReviewProduct> reviewProduct;

    @ManyToOne
    @JoinColumn(name = "type_gold_id")
    private TypeGold typeGold;

    private boolean active;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<ProductImage> productImages;

    @Enumerated(EnumType.STRING)
    @Column(length = 100)
    private EGoldOptionType typeGoldOption;

    public Product() {
    }

    public Product(String productName, String description, Double weight, BigDecimal processingCost, Long totalUnitOfStock,
                   Long unitOfStock, Long totalProductSold, ProductCategory category, TypeGold typeGold, EGoldOptionType typeGoldOption, boolean active) {
        this.productName = productName;
        this.description = description;
        this.weight = weight;
        this.processingCost = processingCost;
        this.totalUnitOfStock = totalUnitOfStock;
        this.unitOfStock = unitOfStock;
        this.totalProductSold = totalProductSold;
        this.category = category;
        this.typeGold = typeGold;
        this.typeGoldOption = typeGoldOption;
        this.active = active;
    }

    @Override
    public int compareTo(Product product) {
        return this.getTypeGold().getPrice().compareTo(product.getTypeGold().getPrice());
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

    public BigDecimal getProcessingCost() {
        return processingCost;
    }

    public void setProcessingCost(BigDecimal processingCost) {
        this.processingCost = processingCost;
    }

    public Long getUnitOfStock() {
        return unitOfStock;
    }

    public void setUnitOfStock(Long unitOfStock) {
        this.unitOfStock = unitOfStock;
    }

    public ProductCategory getCategory() {
        return category;
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
    }

    public List<ReviewProduct> getReviewProduct() {
        return reviewProduct;
    }

    public Double getAvgReview() {
        if (reviewProduct == null) return 0D;
        return reviewProduct.stream().mapToDouble(ReviewProduct::getNumOfReviews).average().orElse(0d);
    }

    public void setReviewProduct(List<ReviewProduct> reviewProduct) {
        this.reviewProduct = reviewProduct;
    }

    public Long getTotalUnitOfStock() {
        return totalUnitOfStock;
    }

    public void setTotalUnitOfStock(Long totalUnitOfStock) {
        this.totalUnitOfStock = totalUnitOfStock;
    }

    public Long getTotalProductSold() {
        return totalProductSold;
    }

    public void setTotalProductSold(Long totalProductSold) {
        this.totalProductSold = totalProductSold;
    }

    public Double getPercentageReduce() {
        return percentageReduce;
    }

    public void setPercentageReduce(Double percentageReduce) {
        this.percentageReduce = percentageReduce;
    }

    public BigDecimal getSecondPrice() {
        return secondPrice;
    }

    public void setSecondPrice(BigDecimal secondPrice) {
        this.secondPrice = secondPrice;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public TypeGold getTypeGold() {
        return typeGold;
    }

    public void setTypeGold(TypeGold typeGold) {
        this.typeGold = typeGold;
    }

    public List<ProductImage> getProductImages() {
        return productImages;
    }

    public void setProductImages(List<ProductImage> productImages) {
        this.productImages = productImages;
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
}
