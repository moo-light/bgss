package com.server.blockchainserver.payload.request.shopping_request;

import com.server.blockchainserver.models.enums.EProductImageType;
import org.springframework.web.multipart.MultipartFile;

public class ProductImageRequest {
    private String[] oldImgs = new String[]{};
    private MultipartFile[] newImgs = new MultipartFile[]{};
    private EProductImageType[] indexes = new EProductImageType[]{};
    public ProductImageRequest() {
    }

    public ProductImageRequest(String[] oldImgs, MultipartFile[] newImgs) {
        this.oldImgs = oldImgs;
        this.newImgs = newImgs;
    }

    public String[] getOldImgs() {
        return oldImgs;
    }

    public void setOldImgs(String[] oldImgs) {
        this.oldImgs = oldImgs;
    }

    public MultipartFile[] getNewImgs() {
        return newImgs;
    }

    public void setNewImgs(MultipartFile[] newImgs) {
        this.newImgs = newImgs;
    }

    public EProductImageType[] getIndexes() {
        return indexes;
    }

    public void setIndexes(EProductImageType[] indexes) {
        this.indexes = indexes;
    }
}
