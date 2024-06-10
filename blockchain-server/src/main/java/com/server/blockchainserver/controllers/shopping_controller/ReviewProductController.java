package com.server.blockchainserver.controllers.shopping_controller;

import com.server.blockchainserver.advices.Response;
import com.server.blockchainserver.dto.shopping_dto.ReviewProductDTO;
import com.server.blockchainserver.exeptions.NotFoundException;
import com.server.blockchainserver.exeptions.ReviewProductException;
import com.server.blockchainserver.models.shopping_model.Order;
import com.server.blockchainserver.models.shopping_model.ReviewProduct;
import com.server.blockchainserver.payload.request.shopping_request.ReviewProductRequest;
import com.server.blockchainserver.services.Services;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@CrossOrigin(value = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class ReviewProductController {

    @Autowired
    Services reviewProdcutService;

    @GetMapping("/product/{productId}/check-review")
    public ResponseEntity<Response> checkReview(@RequestParam Long userId, @PathVariable Long productId) {
        try {
            List<Order> orderList = reviewProdcutService.checkProductReceived(userId, productId);
            ReviewProduct review = reviewProdcutService.checkReview(userId, productId);

            if (!orderList.isEmpty()) {
                if (review == null) {
                    Response response = new Response(HttpStatus.OK, "The user has received this product and can review product right now.", true);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    Response response = new Response(HttpStatus.OK, "The user has reviewed this product, can update review.", false);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            } else {
                Response response = new Response(HttpStatus.NOT_ACCEPTABLE, "The user does not receive this product, cannot review this product.", null);
                return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
            }
        } catch (ReviewProductException e) {
            Response response = new Response(HttpStatus.BAD_REQUEST, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred." + e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/product/{productId}/get-all-review")
    public ResponseEntity<Response> getAllReviewInProduct(@PathVariable Long productId) {
        try {
            List<ReviewProductDTO> reviewList = reviewProdcutService.getAllReviewInProduct(productId);
            if(reviewList.isEmpty()){
                Response response = new Response(HttpStatus.OK, "The list review of product is empty.", reviewList);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }

            Response response = new Response(HttpStatus.OK, "Show list reviews of product success.", reviewList);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ReviewProductException e) {
            Response response = new Response(HttpStatus.BAD_REQUEST, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred." + e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-all-review-all-product")
    @PreAuthorize("hasRole('ROLE_ADMIN')  or hasRole('ROLE_STAFF')")
    public ResponseEntity<Response> getAllReviewOfAllProduct(@RequestParam(required = false, defaultValue = "") String search) {
        try {
            List<ReviewProductDTO> reviewList = reviewProdcutService.getAllReviewOfAllProduct(search);
            if(reviewList.isEmpty()){
                Response response = new Response(HttpStatus.OK, "The list review of product is empty.", reviewList);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }

            Response response = new Response(HttpStatus.OK, "Show list reviews of all product success.", reviewList);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred." + e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/product/{productId}/get-user-review/{userId}")
    public ResponseEntity<Response> getReviewInProductByUserId(@PathVariable long productId,
                                                               @PathVariable long userId) {
        try {
            ReviewProduct review = reviewProdcutService.checkReview(userId, productId);
            Response response = new Response(HttpStatus.OK, "Show list reviews of product success.", review);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred." + e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/create-review")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @Transactional
    public ResponseEntity<Response> createReview(@RequestParam Long userId, @RequestParam Long productId, @ModelAttribute ReviewProductRequest request, @RequestParam(required = false) MultipartFile imgReview) {
        try {
            ReviewProductDTO createReview = reviewProdcutService.createReview(userId, productId, request, imgReview);
            Response response = new Response(HttpStatus.OK, "User create a review for product success", createReview);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ReviewProductException | IOException e) {
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

    @PutMapping("/update-review")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @Transactional
    public ResponseEntity<Response> updateReview(@RequestParam Long userId, @RequestParam Long productId, @ModelAttribute ReviewProductRequest request, @RequestParam(required = false) MultipartFile imgReview) {
        try {
            ReviewProductDTO updateReview = reviewProdcutService.updateReview(userId, productId, request, imgReview);
            Response response = new Response(HttpStatus.OK, "User update review for product success", updateReview);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ReviewProductException | IOException e) {
            Response response = new Response(HttpStatus.BAD_REQUEST, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred." + e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete-review/{reviewProductId}")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<Response> deleteReview(@PathVariable Long reviewProductId, @RequestParam Long productId, @RequestParam Long userId) {
        try {
            boolean deleteReview = reviewProdcutService.deleteReviewed(reviewProductId, productId, userId);
            Response response = new Response(HttpStatus.OK, "Delete review of product success.", deleteReview);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (NotFoundException e) {
            Response response = new Response(HttpStatus.NOT_FOUND, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (ReviewProductException e) {
            Response response = new Response(HttpStatus.BAD_REQUEST, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred." + e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
