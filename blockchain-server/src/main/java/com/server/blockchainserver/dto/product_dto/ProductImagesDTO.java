package com.server.blockchainserver.dto.product_dto;

public class ProductImagesDTO {

    private Long id;

    private String imgUrl;

    private Long productId;

    public ProductImagesDTO() {
    }

    public ProductImagesDTO(Long id, String imgUrl, Long productId) {
        this.id = id;
        this.imgUrl = imgUrl;
        this.productId = productId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}
