package com.server.blockchainserver.controllers.shopping_controller;

import com.server.blockchainserver.advices.Response;
import com.server.blockchainserver.exeptions.DiscountCodeException;
import com.server.blockchainserver.exeptions.NotFoundException;
import com.server.blockchainserver.models.shopping_model.Discount;
import com.server.blockchainserver.payload.request.shopping_request.DiscountCodeRequest;
import com.server.blockchainserver.services.Services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class DiscountController {

    @Autowired
    Services discountService;

    @GetMapping("/get-all-discount-code")
    public ResponseEntity<Response> getAllDiscountCode() {
        try {
            List<Discount> listCode = discountService.getAllDiscountCode();
            if(listCode.isEmpty()){
                Response response = new Response(HttpStatus.OK, "The list discount code is empty.", listCode);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }

            Response response = new Response(HttpStatus.OK, "Show list discount code success.", listCode);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (DiscountCodeException e) {
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

    @GetMapping("/get-discount-code-by-id/{discountCodeId}")
    public ResponseEntity<Response> getDiscountCodeById(@PathVariable Long discountCodeId) {
        try {
            Discount discount = discountService.getDiscountCodeById(discountCodeId);
            Response response = new Response(HttpStatus.OK, "Show a discount code success.", discount);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new Response(HttpStatus.NOT_FOUND, e.getMessage(), null).getResponseEntity();
        } catch (Exception e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred." + e.getMessage(), null).getResponseEntity();
        }
    }

//    @GetMapping("/get-discount-code-by-code")
//    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
//    public ResponseEntity<Response> getDiscountCodeByCode(@RequestParam(defaultValue = "") String code,@RequestParam Long userId) {
//        try {
//            Discount discount = discountService.getDiscountCodeByCode(code,userId);
//            if(discount == null) return new Response(HttpStatus.NOT_FOUND,"Discount code not found!").getResponseEntity();
//            Response response = new Response(HttpStatus.OK, "Show a discount code success.", discount);
//            return new ResponseEntity<>(response, HttpStatus.OK);
//        } catch (NotFoundException e) {
//            return new Response(HttpStatus.NOT_FOUND, e.getMessage(), null).getResponseEntity();
//        } catch (Exception e) {
//            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred." + e.getMessage(), null).getResponseEntity();
//        }
//    }

    @PostMapping("/create-discount-code")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> createDiscountCode(@RequestBody DiscountCodeRequest discountCodeRequest) {
        try {
            Discount checkCreate = discountService.createDiscountCode(discountCodeRequest);
            Response response = new Response(HttpStatus.OK, "Create a discount code success.", checkCreate);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (DiscountCodeException e) {
            Response response = new Response(HttpStatus.BAD_REQUEST, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred." + e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete-discount-code/{discountId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> deleteDiscountCode(@PathVariable Long discountId) {
        try {
            boolean checkDelete = discountService.deleteDiscountCode(discountId);
            Response response = new Response(HttpStatus.OK, "Delete a discount code success.", checkDelete);
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
