package com.server.blockchainserver.controllers.shopping_controller;

import com.server.blockchainserver.advices.Response;
import com.server.blockchainserver.exeptions.DiscountCodeOfUserException;
import com.server.blockchainserver.exeptions.NotFoundException;
import com.server.blockchainserver.models.shopping_model.DiscountCodeOfUser;
import com.server.blockchainserver.services.Services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class DiscountCodeOfUserController {

    @Autowired
    Services discountCodeOfuserService;

    @GetMapping("/get-all-discount-code-of-user-by-userId")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<Response> getAllDiscountCodeOfUserByUserId(@RequestParam Long userId,@RequestParam(defaultValue = "false") Optional<Boolean> expire, @RequestParam(required = false, defaultValue = "") String search) {
        try {
            List<DiscountCodeOfUser> listCodeOfUser = discountCodeOfuserService.getAllDiscountCodeOfUser(userId,expire, search);
            if(listCodeOfUser.isEmpty()){
                Response response = new Response(HttpStatus.OK, "The list discount code of user collected is empty.", listCodeOfUser);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }

            Response response = new Response(HttpStatus.OK, "Show list discount code of user collected success.", listCodeOfUser);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (DiscountCodeOfUserException e) {
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

    @GetMapping("/get-discount-code-of-user-by-id/{discountCodeOfUserId}")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<Response> getDiscountCodeOfUserById(@RequestParam Long userId, @PathVariable Long discountCodeOfUserId) {
        try {
            DiscountCodeOfUser codeOfUser = discountCodeOfuserService.getDiscountCode(userId, discountCodeOfUserId);
            Response response = new Response(HttpStatus.OK, "Show a discount code of user collected success.", codeOfUser);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (NotFoundException e) {
            Response response = new Response(HttpStatus.NOT_FOUND, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred." + e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/create-discount-code-of-user")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<Response> createDiscountCodeOfUser(@RequestParam Long discountId, @RequestParam Long userId) {
        try {
            DiscountCodeOfUser checkCreate = discountCodeOfuserService.createDiscountCodeOfUser(discountId, userId);
            Response response = new Response(HttpStatus.OK, "Create a discount code of user success.", checkCreate);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (DiscountCodeOfUserException e) {
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

    @DeleteMapping("/delete-discount-code-of-user/{discountCodeOfUserId}")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<Response> deleteDiscountCode(@RequestParam Long userId, @PathVariable Long discountCodeOfUserId) {
        try {
            boolean checkDelete = discountCodeOfuserService.deleteDiscountCodeExpired(userId, discountCodeOfUserId);
            Response response = new Response(HttpStatus.OK, "Delete a discount code of user success.", checkDelete);
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
