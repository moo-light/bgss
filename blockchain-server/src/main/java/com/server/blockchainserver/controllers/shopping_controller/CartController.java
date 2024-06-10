package com.server.blockchainserver.controllers.shopping_controller;

import com.server.blockchainserver.advices.Response;
import com.server.blockchainserver.exeptions.CartException;
import com.server.blockchainserver.exeptions.NotFoundException;
import com.server.blockchainserver.models.shopping_model.Cart;
import com.server.blockchainserver.models.shopping_model.CartItem;
import com.server.blockchainserver.payload.request.shopping_request.ProductToCartRequest;
import com.server.blockchainserver.services.Services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class CartController {

    @Autowired
    Services cartService;

    @GetMapping("/get-list-cart-fellow-userid")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<?> getListCartByCartId(@RequestParam Long userId) {
        try {
            if (userId == null) {
                Response response = new Response(HttpStatus.NOT_FOUND, "This is guess so userid is null", null);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            List<CartItem> cartItemList = cartService.getListCartItemOfUser(userId);
            Response response = new Response(HttpStatus.OK, "Show list item in cart success.", cartItemList);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (CartException e) {
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

//    @GetMapping("/get-list-cart-item-fellow-userid")
//    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
//    public ResponseEntity<?> getListCartItemByCartId(@RequestParam Long userId) {
//
//        List<CartItem> cartItemList = cartService.getListCartItem(userId);
//
//        try {
//            if(cartItemList.isEmpty()){
//
//            if (userId == null) {
//                Response response = new Response(HttpStatus.NOT_FOUND, "This is guess so userid is null", Collections.emptyList());
//                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
//            }
//                Response response = new Response(HttpStatus.OK, "Cart empty.", Collections.emptyList());
//                return new ResponseEntity<>(response, HttpStatus.OK);
//            }
//
//            Response response = new Response(HttpStatus.OK, "Show list item in cart success.", cartItemList);
//            return new ResponseEntity<>(response, HttpStatus.OK);
//
//        } catch (NotFoundException e) {
//            Response response = new Response(HttpStatus.NOT_FOUND, e.getMessage(), Collections.emptyList());
//            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
//        } catch (Exception e) {
//            Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred." + e.getMessage(), Collections.emptyList());
//            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    @GetMapping("/get-cart-by-userId/{userId}")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<?> getCartByUserId(@PathVariable Long userId) {
        try {
            Cart cart = cartService.getCartByUserId(userId);
            Response response = new Response(HttpStatus.OK, "Get cart by userId: " + userId + " success", cart);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (CartException e) {
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

    @PostMapping("/add-product-to-cart")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<?> addProductToCart(@RequestBody ProductToCartRequest productToCartRequest) {
        try {
            CartItem addProductToCart = cartService.addProductToCart(productToCartRequest);
            Response response = new Response(HttpStatus.OK, "Add product to cart success.", addProductToCart);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (CartException e) {
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

    @PutMapping("/update-quantity/{cartItemId}")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<?> updateQuantityProductInCart(@PathVariable Long cartItemId, @RequestParam Long quantity) {
        try {
            CartItem cartItem = cartService.updateQuantityProductInCartItem(cartItemId, quantity);
            Response response = new Response(HttpStatus.OK, "The quantity of product in cart already update success.", cartItem);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (CartException e) {
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

    @DeleteMapping("/remove-product-from-cart/{cartItemId}")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<?> removeProductFromCart(@PathVariable Long cartItemId) {
        try {
            boolean checkRemove = cartService.removeProductFromCart(cartItemId);
            Response response = new Response(HttpStatus.OK, "Remove product from cart success.", checkRemove);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (CartException e) {
            Response response = new Response(HttpStatus.BAD_REQUEST, e.getMessage(), false);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (NotFoundException e) {
            Response response = new Response(HttpStatus.NOT_FOUND, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred." + e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
