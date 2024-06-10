package com.server.blockchainserver.controllers.shopping_controller;

import com.server.blockchainserver.advices.Constants;
import com.server.blockchainserver.advices.Response;
import com.server.blockchainserver.exeptions.NotFoundException;
import com.server.blockchainserver.exeptions.OrderException;
import com.server.blockchainserver.exeptions.VerifyOTPOrderException;
import com.server.blockchainserver.models.shopping_model.Order;
import com.server.blockchainserver.payload.request.CreateOrderNowRequest;
import com.server.blockchainserver.payload.request.shopping_request.CreateOrderRequest;
import com.server.blockchainserver.repository.shopping_repository.OrderRepository;
import com.server.blockchainserver.services.MailSenderService;
import com.server.blockchainserver.services.Services;
import com.server.blockchainserver.utils.AuthenticationHelper;
import com.server.blockchainserver.utils.ObjectHelper;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(value = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class OrderController {
    @Autowired
    Services orderService;
    @Autowired
    private MailSenderService mailSenderService;
    @Autowired
    private OrderRepository orderRepository;

    @PostMapping("/create-order/{userId}")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @Transactional
    public ResponseEntity<?> createOrder(@PathVariable Long userId, @RequestBody CreateOrderRequest createOrderRequest) {
        try {
            Order order = orderService.createOrder(userId, createOrderRequest);
            mailSenderService.generateAndSendOtp(order);
            if (order != null) {
                return new Response(HttpStatus.OK, "Create order success", order).getResponseEntity();
            } else {
                return new Response(HttpStatus.valueOf(404), "order not found", null).getResponseEntity();
            }
        } catch (Exception e) {
            return Response.getResponseFromException(e);
        }
    }

//    @PostMapping("/create-order-now/{userId}")
//    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
//    @Transactional
//    public ResponseEntity<?> createOrderNow(@PathVariable Long userId, @RequestBody CreateOrderNowRequest request) {
//        try {
//            Order order = orderService.createOrderNow(userId, request);
//            mailSenderService.generateAndSendOtp(order);
//            if (order != null) {
//                return new Response(HttpStatus.OK, "Create order success", order).getResponseEntity();
//            } else {
//                return new Response(HttpStatus.valueOf(404), "order not found", null).getResponseEntity();
//            }
//        } catch (Exception e) {
//            return Response.getResponseFromException(e);
//        }
//    }

    @GetMapping("/verification-order/{otp}/{orderId}")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @Transactional
    public ResponseEntity<Response> otpVerificationOrder(@PathVariable String otp,
                                                         @PathVariable Long orderId) {
        try {
            boolean isVerified = mailSenderService.verifyOtpForOrder(otp, orderId);
            if (!isVerified) {
                Response response = new Response(HttpStatus.NOT_FOUND, "OTP is incorrect! Please try again");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            if (otp == null || otp.isEmpty()) {
                throw new NotFoundException("OTP is null or empty");
            }
            Response response = new Response(HttpStatus.OK, "Order Verified! please come to the store to receive your Order!", true);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (VerifyOTPOrderException e) {
            Response response = new Response(HttpStatus.BAD_REQUEST, e.getMessage(), false);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), false);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/resent-otp/{orderId}")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<Response> resentOtpOrder(@PathVariable Long orderId) {
        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new NotFoundException("Order not found with ID: " + orderId));
            mailSenderService.generateOtpForOrder(order);
            Response response = new Response(HttpStatus.OK, "OTP has been resent successfully.", true);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), false);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-order-list")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STAFF') or hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<?> getOrderList(@RequestParam Optional<Long> userId) {
        try {
            // Validations
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Map<String, Object> principal = ObjectHelper.convertObjectToHashMap(authentication.getPrincipal());
            boolean higherPriority = AuthenticationHelper.hasRoles(authentication, Constants.ROLE_ADMIN, Constants.ROLE_STAFF);


            List<Order> orders;
            if (higherPriority) {
                orders = orderService.getOrderList(userId);
                if(orders.isEmpty()){
                    return new Response(HttpStatus.OK, "The order list is empty.", orders).getResponseEntity();
                }

                return new Response(HttpStatus.OK, "Get order List success.", orders).getResponseEntity();
            }

            if (!userId.isPresent()) {
                return new Response(HttpStatus.BAD_REQUEST, "Error", "User is not allowed to get orders from other users").getResponseEntity();
            }

            if (userId.get().equals(principal.get("id"))) {
                orders = orderService.getOrderList(userId);
                if(orders.isEmpty()){
                    return new Response(HttpStatus.OK, "The order list is empty.", orders).getResponseEntity();
                }
                
                return new Response(HttpStatus.OK, "Get order List success", orders).getResponseEntity();
            } else {
                return new Response(HttpStatus.BAD_REQUEST, "Error", "User is not allowed to get orders from other users").getResponseEntity();
            }
        } catch (Exception e) {
            // Log the exception for debugging purposes
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, "Error", "Failed to fetch order list").getResponseEntity();
        }
    }

    @GetMapping("/get-order-by-id/{orderId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STAFF') or hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<?> getOrderDetail(@PathVariable Long orderId) throws Exception {
        // Validations
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> principal = ObjectHelper.convertObjectToHashMap(authentication.getPrincipal());

        try {
            Order order;
            boolean isCustomer = AuthenticationHelper.hasRoles(authentication, Constants.ROLE_CUSTOMER);
            order = orderService.getOrderById(orderId);
            if (isCustomer) {
                if (!order.getUser().getId().equals(principal.get("id"))) {
                    return new Response(HttpStatus.FORBIDDEN, "User are not allowed to get order from other user", "User are not allowed to get order from other user").getResponseEntity();
                }
            }
            return new Response(HttpStatus.OK, "Get order success", order).getResponseEntity();
        } catch (Exception e) {
            return Response.getResponseFromException(e);
        }
    }

    @GetMapping("/search-order-by-qr_code")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STAFF')")
    public ResponseEntity<Response> searchOrderByCode(@RequestParam String qr_Code) {
        try {
            Order order = orderService.searchOrderByQrCode(qr_Code);
            Response response = new Response(HttpStatus.OK, "Show a order with qr_code: " + qr_Code + " success.", order);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (OrderException e) {
            Response response = new Response(HttpStatus.BAD_REQUEST, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (NotFoundException e) {
            Response response = new Response(HttpStatus.NOT_FOUND, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        catch (Exception e) {
            Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred." + e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update-status-received/{orderId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STAFF')")
    public ResponseEntity<Response> updateStatusReceived(@PathVariable Long orderId) {
        try {
            Order order = orderService.updateStatusReceived(orderId);
            Response response = new Response(HttpStatus.OK, "Update status in order when user received product success.", order);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (OrderException e) {
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

    @PutMapping("/user-confirm/{orderId}")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<Response> userConfirmReceived(@PathVariable Long orderId) {
        try {
            Order order = orderService.userConfirmReceived(orderId);
            Response response = new Response(HttpStatus.OK, "User confirm received product success.", order);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (OrderException e) {
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
}
