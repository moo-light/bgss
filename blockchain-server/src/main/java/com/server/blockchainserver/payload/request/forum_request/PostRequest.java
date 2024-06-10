package com.server.blockchainserver.payload.request.forum_request;

public class PostRequest {
    private String title;
    private String content;
    private boolean isPinned;
    private Long categoryPostId;

    public PostRequest() {
    }

    public PostRequest(String title, String content, boolean isPinned, Long categoryPostId) {
        this.title = title;
        this.content = content;
        this.isPinned = isPinned;
        this.categoryPostId = categoryPostId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getCategoryPostId() {
        return categoryPostId;
    }

    public void setCategoryPostId(Long categoryPostId) {
        this.categoryPostId = categoryPostId;
    }

    public boolean getIsPinned() {
        return isPinned;
    }

    public void setPinned(boolean pinned) {
        isPinned = pinned;
    }


}
