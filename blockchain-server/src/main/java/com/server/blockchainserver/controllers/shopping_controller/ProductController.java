package com.server.blockchainserver.controllers.shopping_controller;

import com.server.blockchainserver.advices.Response;
import com.server.blockchainserver.dto.product_dto.ProductDTO;
import com.server.blockchainserver.exeptions.NotFoundException;
import com.server.blockchainserver.exeptions.ProductException;
import com.server.blockchainserver.models.shopping_model.Product;
import com.server.blockchainserver.payload.request.shopping_request.ProductImageRequest;
import com.server.blockchainserver.payload.request.shopping_request.ProductRequest;
import com.server.blockchainserver.services.Services;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class ProductController {
    @Autowired
    private Services productServices;

    @GetMapping("/product/show-list-product")
    public Response getAllProduct_WithSearch_AndFilterProductByPriceAscOrDesc(
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(required = false) Optional<Boolean> asc,
            @RequestParam(name = "price_gte") Optional<Long> minPrice,
            @RequestParam(name = "price_lte") Optional<Long> maxPrice,
            @RequestParam(name = "reviews") Optional<Integer> avgReview,
            @RequestParam(name = "category") Optional<List<Long>> categoryIDS,
            @RequestParam(name = "typeGold") Optional<List<Long>> typeGoldIDS,
            @RequestParam(required = false, defaultValue = "") String typeOptionName) {
        try {
            if (minPrice.isPresent() && maxPrice.isPresent()) {
                if (minPrice.get() > maxPrice.get())
                    throw new Exception("min price can't be larger than max price");
            }
            List<ProductDTO> productList = productServices.filterProductWithSort(search, asc,
                    minPrice, maxPrice, avgReview, categoryIDS, typeGoldIDS, typeOptionName);

            if (!productList.isEmpty()) {
                return new Response(HttpStatus.OK, "Show all product success.", productList);
            } else {
                return new Response(HttpStatus.OK, "The list product is empty.", productList);
            }
        } catch (Exception e) {
            return Response.getResponseFromException_2(e);
        }
    }


    @GetMapping("/product/get-product-by-id/{id}")
    public ResponseEntity<Response> getProduct(@PathVariable Long id) {
        try {
            ProductDTO product = productServices.getProductById(id);
            Response response = new Response(HttpStatus.OK, "Show a product success.", product);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (NotFoundException e) {
            Response response = new Response(HttpStatus.NOT_FOUND, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred." + e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/add-product")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public ResponseEntity<Response> addProduct(@RequestBody ProductRequest productRequest) {
        try {
            ProductDTO checkAddProduct = productServices.addNewProduct(productRequest);
            Response response = new Response(HttpStatus.OK, "Add a product success.", checkAddProduct);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ProductException e) {
            Response response = new Response(HttpStatus.BAD_REQUEST, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (NotFoundException e) {
            Response response = new Response(HttpStatus.NOT_FOUND, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred." + e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/product/update-product-image/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public ResponseEntity<Response> productImageUpdate(@PathVariable Long id, @ModelAttribute ProductImageRequest images) {
        try {
            //check for index
            if (images.getIndexes().length == 0)
                return new Response(HttpStatus.BAD_REQUEST, "missing indexes!").getResponseEntity();
            //check for lengths
            if (images.getOldImgs().length + images.getNewImgs().length != images.getIndexes().length)
                return new Response(HttpStatus.BAD_REQUEST, "indexes not match!").getResponseEntity();
            //check for length 5
            if (images.getIndexes().length > 5)
                return new Response(HttpStatus.BAD_REQUEST, "only maximum of 5 images are allowed").getResponseEntity();

            Response response = new Response(HttpStatus.OK, "Update image product success.", productServices.updateProductImgUrl(id, images));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (NotFoundException e) {
            Response response = new Response(HttpStatus.NOT_FOUND, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred." + e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/product/update-product/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> updateProduct(@PathVariable Long id, @RequestBody ProductRequest productRequest) {
        try {
            ProductDTO checkUpdate = productServices.updateProduct(id, productRequest);
            Response response = new Response(HttpStatus.OK, "Update a product success.", checkUpdate);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ProductException e) {
            Response response = new Response(HttpStatus.BAD_REQUEST, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (NotFoundException e) {
            Response response = new Response(HttpStatus.NOT_FOUND, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred." + e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/product/delete-product/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> deleteProduct(@PathVariable Long id) {
        try {
            boolean checkDelete = productServices.deleteProduct(id);
            Response response = new Response(HttpStatus.OK, "Delete a product success.", checkDelete);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (NotFoundException e) {
            Response response = new Response(HttpStatus.NOT_FOUND, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred." + e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/product/filter-product-by-categoryName")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STAFF')")
    public ResponseEntity<Response> filterProductByCategoryName(@RequestParam List<Long> categorIdList) {
        try {
            List<Product> filterProductList = productServices.filterProductByCategoryName(categorIdList);
            Response response = new Response(HttpStatus.OK, "Filter product with category name success", filterProductList);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred." + e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-all-product-by-type-gold-name")
    public ResponseEntity<Response> getAllProductByTypeGoldName(@RequestParam String typeGoldName, @RequestParam(required = false) String search) {
        try {
            List<Product> productList = productServices.getAllProductByTypeName(typeGoldName, search);
            if (productList.isEmpty()) {
                Response response = new Response(HttpStatus.OK, "The list product with type name of gold is empty.", Collections.EMPTY_LIST);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }

            Response response = new Response(HttpStatus.OK, "Get all product with type name of gold success.", productList);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ProductException e) {
            Response response = new Response(HttpStatus.BAD_REQUEST, "Error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
