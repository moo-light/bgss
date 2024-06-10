package com.server.blockchainserver.models.shopping_model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.server.blockchainserver.utils.ImageHelper;
import jakarta.persistence.*;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Entity
@Table(name = "product_images")
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imgUrl;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonIgnore
    private Product product;

    public ProductImage() {
    }

    public ProductImage(String imgUrl, Product product) {
        this.imgUrl = imgUrl;
        this.product = product;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public int getIndex() {
        if (StringUtils.hasText(imgUrl)) {
            char imgIndex = imgUrl.charAt(imgUrl.lastIndexOf("_") + 1);
            return Integer.parseInt(Character.toString(imgIndex));
        }
        return -1;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    protected String getFileName() {
        return ImageHelper.getFileName(imgUrl); // Using this make both postMan and web compatible
    }

    public static String replaceIndex(String imgUrl, Integer index) {
        return imgUrl.replaceFirst("(?<=_)\\d", index.toString());
    }

    public boolean move(String newImgUrl, String root) {
        try {
            File image = ImageHelper.searchForFile(root, "Product", getFileName());

            if (image != null && Files.exists(image.toPath())) {
                Files.move(image.toPath(), Paths.get(root, "Product", ImageHelper.getFileName(newImgUrl)), StandardCopyOption.REPLACE_EXISTING);
            }
            setImgUrl(newImgUrl);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public Path remove(String root) {
        try {
            File image = ImageHelper.searchForFile(root, "Product", getFileName());

            if (image != null) {
                Files.delete(image.toPath());
            } else {
                return null;
            }
            return image.toPath();
        } catch (IOException e) {
            return null;
        }
    }
}
