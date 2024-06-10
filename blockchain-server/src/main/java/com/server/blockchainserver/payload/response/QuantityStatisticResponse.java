package com.server.blockchainserver.payload.response;

public class QuantityStatisticResponse {
    private int quantityUser;
    private int quantityActiveUser;
    private int quantityInactiveUser;
    private int quantityVerifiedUser;
    private int quantityUnverifiedUser;

    private int quantityPost;
    private int quantityProduct;
    private int quantityReviewOneStar;
    private int quantityReviewTwoStar;
    private int quantityReviewThreeStar;
    private int quantityReviewFourStar;
    private int quantityReviewFiveStar;

    public QuantityStatisticResponse() {
    }

    public QuantityStatisticResponse(int quantityUser, int quantityActiveUser, int quantityInactiveUser,
                                     int quantityVerifiedUser, int quantityUnverifiedUser, int quantityPost,
                                     int quantityProduct, int quantityReviewOneStar, int quantityReviewTwoStar,
                                     int quantityReviewThreeStar, int quantityReviewFourStar, int quantityReviewFiveStar) {
        this.quantityUser = quantityUser;
        this.quantityActiveUser = quantityActiveUser;
        this.quantityInactiveUser = quantityInactiveUser;
        this.quantityVerifiedUser = quantityVerifiedUser;
        this.quantityUnverifiedUser = quantityUnverifiedUser;
        this.quantityPost = quantityPost;
        this.quantityProduct = quantityProduct;
        this.quantityReviewOneStar = quantityReviewOneStar;
        this.quantityReviewTwoStar = quantityReviewTwoStar;
        this.quantityReviewThreeStar = quantityReviewThreeStar;
        this.quantityReviewFourStar = quantityReviewFourStar;
        this.quantityReviewFiveStar = quantityReviewFiveStar;
    }

    public int getQuantityUser() {
        return quantityUser;
    }

    public void setQuantityUser(int quantityUser) {
        this.quantityUser = quantityUser;
    }

    public int getQuantityPost() {
        return quantityPost;
    }

    public void setQuantityPost(int quantityPost) {
        this.quantityPost = quantityPost;
    }

    public int getQuantityProduct() {
        return quantityProduct;
    }

    public void setQuantityProduct(int quantityProduct) {
        this.quantityProduct = quantityProduct;
    }

    public int getQuantityReviewOneStar() {
        return quantityReviewOneStar;
    }

    public void setQuantityReviewOneStar(int quantityReviewOneStar) {
        this.quantityReviewOneStar = quantityReviewOneStar;
    }

    public int getQuantityReviewTwoStar() {
        return quantityReviewTwoStar;
    }

    public void setQuantityReviewTwoStar(int quantityReviewTwoStar) {
        this.quantityReviewTwoStar = quantityReviewTwoStar;
    }

    public int getQuantityReviewThreeStar() {
        return quantityReviewThreeStar;
    }

    public void setQuantityReviewThreeStar(int quantityReviewThreeStar) {
        this.quantityReviewThreeStar = quantityReviewThreeStar;
    }

    public int getQuantityReviewFourStar() {
        return quantityReviewFourStar;
    }

    public void setQuantityReviewFourStar(int quantityReviewFourStar) {
        this.quantityReviewFourStar = quantityReviewFourStar;
    }

    public int getQuantityReviewFiveStar() {
        return quantityReviewFiveStar;
    }

    public void setQuantityReviewFiveStar(int quantityReviewFiveStar) {
        this.quantityReviewFiveStar = quantityReviewFiveStar;
    }

    public int getQuantityActiveUser() {
        return quantityActiveUser;
    }

    public void setQuantityActiveUser(int quantityActiveUser) {
        this.quantityActiveUser = quantityActiveUser;
    }

    public int getQuantityInactiveUser() {
        return quantityInactiveUser;
    }

    public void setQuantityInactiveUser(int quantityInactiveUser) {
        this.quantityInactiveUser = quantityInactiveUser;
    }

    public int getQuantityVerifiedUser() {
        return quantityVerifiedUser;
    }

    public void setQuantityVerifiedUser(int quantityVerifiedUser) {
        this.quantityVerifiedUser = quantityVerifiedUser;
    }

    public int getQuantityUnverifiedUser() {
        return quantityUnverifiedUser;
    }

    public void setQuantityUnverifiedUser(int quantityUnverifiedUser) {
        this.quantityUnverifiedUser = quantityUnverifiedUser;
    }
}
