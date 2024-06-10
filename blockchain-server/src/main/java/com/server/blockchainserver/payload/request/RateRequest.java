package com.server.blockchainserver.payload.request;

import org.springframework.web.multipart.MultipartFile;

public class RateRequest {
    private Long userId;
    private Long postId;
    private String content;
    private MultipartFile imageRate;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MultipartFile getImageRate() {
        return imageRate;
    }

    public void setImageRate(MultipartFile imageRate) {
        this.imageRate = imageRate;
    }
}
