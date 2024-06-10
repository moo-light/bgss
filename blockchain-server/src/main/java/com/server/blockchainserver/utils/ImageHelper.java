package com.server.blockchainserver.utils;

import java.io.File;

public class ImageHelper {

    public static File searchForFile(String basePath, String folder, String fileName) {
        File subFolder = new File(basePath, folder);
        if (!subFolder.exists() && !subFolder.mkdirs()) {
            return null; // Trả về null nếu không thể tạo thư mục
        }
        File imageFile = new File(subFolder, fileName);
        if (imageFile.exists() && imageFile.canRead()) {
            return imageFile;
        }
        return null; // Trả về null nếu file không tồn tại hoặc không thể đọc
    }
    public static String getFileName(String url){
        return new File(url).getName();
    }
}
