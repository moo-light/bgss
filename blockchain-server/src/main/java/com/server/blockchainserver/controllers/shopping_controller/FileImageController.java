package com.server.blockchainserver.controllers.shopping_controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class FileImageController {

    @Value("${upload.path}")
    private String uploadPath;
    @Value("${upload.path.root}")
    private String uploadPathRoot;

    @RequestMapping(path = "/Images/{folder}/{fileName}", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> displayImage(@PathVariable String folder,
                                                            @PathVariable String fileName) {

        File imageFile = searchForFile(uploadPath, folder, fileName);
        if (imageFile != null) {
            return serveFile(imageFile);
        }

        // Thử vị trí lưu thứ hai
        imageFile = searchForFile(uploadPathRoot, folder, fileName);
        if (imageFile != null) {
            return serveFile(imageFile);
        }

        // Nếu file không tồn tại ở cả hai vị trí, trả về 404 NOT FOUND
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @GetMapping(path = "/api/auth/Images/{folder}/{fileName}")
    public ResponseEntity<InputStreamResource> displayImage2(@PathVariable String folder,
                                                             @PathVariable String fileName) {
        return this.displayImage(folder, fileName);
    }

    // Phương thức phụ trợ để tìm kiếm file
    private File searchForFile(String basePath, String folder, String fileName) {
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

    // Phương thức phụ trợ để phục vụ file
    private ResponseEntity<InputStreamResource> serveFile(File file) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            String mimeType = Files.probeContentType(file.toPath());
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(mimeType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"" + file.getName() + "\"")
                    .body(new InputStreamResource(fileInputStream));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
