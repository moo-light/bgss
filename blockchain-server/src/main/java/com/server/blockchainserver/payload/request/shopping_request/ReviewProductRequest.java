package com.server.blockchainserver.payload.request.shopping_request;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;

public class ReviewProductRequest {

    @Nullable
    private Integer numOfReviews;

    @Column(columnDefinition = "TEXT")
    private String content;

    public Integer getNumOfReviews() {
        return numOfReviews;
    }

    public void setNumOfReviews(Integer numOfReviews) {
        this.numOfReviews = numOfReviews;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
