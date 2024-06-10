package com.server.blockchainserver.controllers.shopping_controller;

import com.server.blockchainserver.advices.Response;
import com.server.blockchainserver.exeptions.NotFoundException;
import com.server.blockchainserver.exeptions.ProductCategoryException;
import com.server.blockchainserver.models.shopping_model.ProductCategory;
import com.server.blockchainserver.services.Services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class CategoryController {

    @Autowired
    Services categoryService;


    @GetMapping("/get-all-category")
    public ResponseEntity<?> getAllCategory(@RequestParam(required = false, defaultValue = "") String search) {
        try {
                List<ProductCategory> categoryList = categoryService.getAllCategory(search);

                if(categoryList.isEmpty()){
                    Response response = new Response(HttpStatus.OK, "The list category of product is empty.", categoryList);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }

                Response response = new Response(HttpStatus.OK, "Show list category of product success.", categoryList);
                return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (ProductCategoryException e) {
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

    @GetMapping("/get-category-by-id/{categoryId}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long categoryId) {
        try {
            ProductCategory category = categoryService.getCategoryById(categoryId);
            Response response = new Response(HttpStatus.OK, "Show a category of product with categoryId: " + categoryId + " success.", category);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (NotFoundException e) {
            Response response = new Response(HttpStatus.NOT_FOUND, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred." + e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/create-product-category")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> createProductCategory(@RequestParam String categoryName) {
        try {
            ProductCategory checkCreate = categoryService.creatProductCategory(categoryName);
            Response response = new Response(HttpStatus.OK, "Create category of product success.", checkCreate);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ProductCategoryException e) {
            Response response = new Response(HttpStatus.BAD_REQUEST, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred." + e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update-product-category/{categoryId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateProductCategory(@PathVariable Long categoryId, @RequestParam String categoryName) {
        try {
            ProductCategory checkUpdate = categoryService.updateProductCategory(categoryId, categoryName);
            Response response = new Response(HttpStatus.OK, "Update category of product success.", checkUpdate);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (NotFoundException e) {
            Response response = new Response(HttpStatus.NOT_FOUND, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred." + e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete-product-category/{categoryId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteProductCategory(@PathVariable Long categoryId) {
        try {
            boolean checkDelete = categoryService.deleteProductCategory(categoryId);
            Response response = new Response(HttpStatus.OK, "Delete category of product success.", checkDelete);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (NotFoundException e) {
            Response response = new Response(HttpStatus.NOT_FOUND, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred." + e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
