package com.server.blockchainserver.dto.product_dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.server.blockchainserver.dto.shopping_dto.ReviewProductDTO;
import com.server.blockchainserver.models.enums.EGoldOptionType;
import com.server.blockchainserver.models.shopping_model.Product;
import com.server.blockchainserver.models.shopping_model.ProductCategory;
import com.server.blockchainserver.models.shopping_model.ProductImage;
import com.server.blockchainserver.models.shopping_model.TypeGold;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.List;

public class ProductDTO {
    private Long id;
    private String productName;
    private String description;
    private Double weight;
    private BigDecimal processingCost;
    private Long totalUnitOfStock;
    private Long unitOfStock;
    private Long totalProductSold;
    private Double percentageReduce;
    private List<ProductImagesDTO> productImages;
    private BigDecimal secondPrice;
    private ProductCategory category;
    private TypeGold typeGold;
    private Double avgReview = 0D;
    private boolean active;
    private EGoldOptionType typeGoldOption;
    @JsonIgnore
    private List<ReviewProductDTO> reviewProduct;

    private BigDecimal priceProduct;
    public ProductDTO() {

    }

//    public ProductDTO(Product product, ModelMapper mapper) {
//        mapper.map(product, this);
//    }


    public ProductDTO(Long id, String productName, String description, Double weight, BigDecimal processingCost, Long totalUnitOfStock,
                      Long unitOfStock, Long totalProductSold, Double percentageReduce, List<ProductImagesDTO> productImages,
                      BigDecimal secondPrice, ProductCategory category, TypeGold typeGold, Double avgReview,
                      EGoldOptionType typeGoldOption, boolean active, List<ReviewProductDTO> reviewProduct, BigDecimal priceProduct) {
        this.id = id;
        this.productName = productName;
        this.description = description;
        this.weight = weight;
        this.processingCost = processingCost;
        this.totalUnitOfStock = totalUnitOfStock;
        this.unitOfStock = unitOfStock;
        this.totalProductSold = totalProductSold;
        this.percentageReduce = percentageReduce;
        this.productImages = productImages;
        this.secondPrice = secondPrice;
        this.category = category;
        this.typeGold = typeGold;
        this.avgReview = avgReview;
        this.typeGoldOption = typeGoldOption;
        this.active = active;
        this.reviewProduct = reviewProduct;
        this.priceProduct = priceProduct;
    }

    public ProductDTO(Long id, String productName, String description, Long unitOfStock, List<ProductImagesDTO>
            productImages, ProductCategory category, TypeGold typeGold, Double avgReview, boolean active) {
        this.id = id;
        this.productName = productName;
        this.description = description;
        this.unitOfStock = unitOfStock;
        this.productImages = productImages;
        this.category = category;
        this.typeGold = typeGold;
        this.avgReview = avgReview;
        this.active = active;
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


    public Double getAvgReview() {
        if(avgReview == null) {
            if(reviewProduct == null) setAvgReview(0D);
            setAvgReview(reviewProduct.stream().mapToInt(ReviewProductDTO::getNumOfReviews).average().getAsDouble());
        }
        return avgReview;
    }

    public List<ReviewProductDTO> getReviewProduct() {
        return reviewProduct;
    }

    public void setReviewProduct(List<ReviewProductDTO> reviewProduct) {
        this.reviewProduct = reviewProduct;
    }

    public void setAvgReview(Double avgReview) {
        this.avgReview = avgReview;
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

    public List<ProductImagesDTO> getProductImages() {
        if(productImages != null) return productImages.stream().sorted((a,b)->a.getImgUrl().compareTo(b.getImgUrl())).toList();
        return productImages;
    }

    public void setProductImages(List<ProductImagesDTO> productImages) {
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

    public BigDecimal getPriceProduct() {
        return priceProduct;
    }

    public void setPriceProduct(BigDecimal priceProduct) {
        this.priceProduct = priceProduct;
    }
}
