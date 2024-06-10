package com.server.blockchainserver.controllers.forum_controller;

import com.server.blockchainserver.advices.Response;
import com.server.blockchainserver.exeptions.NotFoundException;
import com.server.blockchainserver.exeptions.PostCategoryException;
import com.server.blockchainserver.models.shopping_model.CategoryPost;
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
public class CategoryPostController {

    @Autowired
    Services categoryPostService;

    @GetMapping("/show-all-category-post")
    public ResponseEntity<Response> showAllCategoryPost(@RequestParam(required = false, defaultValue = "") String search) {
        try {
            List<CategoryPost> categoryPostList = categoryPostService.showAllCategoryPost(search);
            if(categoryPostList.isEmpty()){
                Response response = new Response(HttpStatus.OK, "The list category of post is empty.", categoryPostList);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }

            Response response = new Response(HttpStatus.OK, "Show list category of post success.", categoryPostList);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (PostCategoryException e) {
            Response response = new Response(HttpStatus.BAD_REQUEST, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred." + e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-category-post-by-id/{categoryPostId}")
    public ResponseEntity<Response> getCategoryPostById(@PathVariable Long categoryPostId) {
        try {
            CategoryPost categoryPost = categoryPostService.getCategoryPostById(categoryPostId);
            Response response = new Response(HttpStatus.OK, "Show a category of post with id: " + categoryPostId + " success.", categoryPost);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (NotFoundException e) {
            Response response = new Response(HttpStatus.NOT_FOUND, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred." + e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/create-category-post")
    @PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_STAFF')")
    public ResponseEntity<Response> createCategoryPost(@RequestParam String categoryName) {
        try {
            CategoryPost checkCreate = categoryPostService.createCategoryPost(categoryName);
            Response response = new Response(HttpStatus.OK, "Create category of post success.", checkCreate);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (PostCategoryException e) {
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

    @PutMapping("/update-category-post/{categoryPostId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_STAFF')")
    public ResponseEntity<Response> updateCategoryPost(@PathVariable Long categoryPostId, @RequestParam String categoryName) {
        try {
            CategoryPost checkUpdate = categoryPostService.updateCategoryPost(categoryPostId, categoryName);
            Response response = new Response(HttpStatus.OK, "Update category of post success.", checkUpdate);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (PostCategoryException e) {
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

    @DeleteMapping("/delete-category-post/{categoryPostId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_STAFF')")
    public ResponseEntity<Response> deleteCategoryPost(@PathVariable Long categoryPostId) {
        try {
            boolean checkDelete = categoryPostService.deleteCategoryPost(categoryPostId);
            Response response = new Response(HttpStatus.OK, "Delete category of post success.", checkDelete);
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
