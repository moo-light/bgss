package com.server.blockchainserver.controllers.transaction_controller;


import com.server.blockchainserver.advices.Constants;
import com.server.blockchainserver.advices.Response;
import com.server.blockchainserver.exeptions.NotFoundException;
import com.server.blockchainserver.exeptions.UserConfirmWithdrawException;
import com.server.blockchainserver.exeptions.WithdrawalNotAllowedException;
import com.server.blockchainserver.platform.data_transfer_object.ContractDTO;
import com.server.blockchainserver.platform.data_transfer_object.TransactionDTO;
import com.server.blockchainserver.platform.data_transfer_object.WithdrawGoldDTO;
import com.server.blockchainserver.platform.data_transfer_object.WithdrawGoldDefault;
import com.server.blockchainserver.platform.entity.WithdrawGold;
import com.server.blockchainserver.platform.entity.enums.GoldUnit;
import com.server.blockchainserver.platform.entity.enums.TransactionType;
import com.server.blockchainserver.platform.entity.enums.WithdrawRequirement;
import com.server.blockchainserver.platform.payload.response.request.VerifyContractRequest;
import com.server.blockchainserver.platform.platform_services.services_interface.TransactionServices;
import com.server.blockchainserver.platform.repositories.ContractRepository;
import com.server.blockchainserver.platform.repositories.GoldTransactionRepository;
import com.server.blockchainserver.platform.repositories.WithdrawGoldRepository;
import com.server.blockchainserver.services.MailSenderService;
import com.server.blockchainserver.utils.AuthenticationHelper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class TransactionController {

    @Autowired
    private TransactionServices transactionServices;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private MailSenderService mailSenderService;

    @Autowired
    private GoldTransactionRepository goldTransactionRepository;
    @Autowired
    private WithdrawGoldRepository withdrawGoldRepository;


    /**
     * Processes a transaction for a user based on the provided details.
     * This method is secured with Spring Security, allowing only users with the 'ROLE_CUSTOMER' role to access it.
     * It attempts to process a transaction through the {@link TransactionServices} and returns a response entity
     * containing the transaction details or an error message.
     *
     * @param userInfoId The ID of the user initiating the transaction.
     * @param quantityInOz The quantity of gold in ounces to be transacted.
     * @param pricePerOz The price per ounce of gold for this transaction.
     * @param type The type of transaction, e.g., BUY or SELL.
     * @param goldUnit The unit of gold being transacted, e.g., OUNCE.
     * @return A {@link ResponseEntity} containing a {@link Response} object with the transaction details if successful,
     *         or an error message if the transaction fails. The HTTP status code reflects the outcome of the operation.
     */
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @PutMapping("/transactions/{userInfoId}")
    public ResponseEntity<Response> transaction(@PathVariable Long userInfoId,
                                                @RequestParam BigDecimal quantityInOz,
                                                @RequestParam BigDecimal pricePerOz,
                                                @RequestParam TransactionType type,
                                                @RequestParam GoldUnit goldUnit) {
        try {
            TransactionDTO transactionDTO = transactionServices.processTransaction(userInfoId, quantityInOz, pricePerOz, type, goldUnit);
            Response response = new Response(HttpStatus.OK, type.toString(), transactionDTO);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (NotFoundException e) {
            Response response = new Response(HttpStatus.NOT_FOUND, e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (IllegalStateException e) {
            Response response = new Response(HttpStatus.PAYMENT_REQUIRED, e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(response);
        } catch (Exception e) {
            Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null);
            return ResponseEntity.internalServerError().body(response);
        }
    }


    /**
     * Verifies the OTP for a transaction.
     * This endpoint is secured with Spring Security, allowing only users with the 'ROLE_CUSTOMER' role to access it.
     * It checks if the provided OTP matches the one generated for the transaction. If the OTP is correct, the transaction
     * is marked as verified. If the OTP is incorrect or not found, it returns an error response.
     *
     * @param otp The one-time password provided by the user for transaction verification.
     * @param transactionId The ID of the transaction to be verified.
     * @return A {@link ResponseEntity} containing a {@link Response} object. If the OTP verification is successful,
     *         the response includes a success message. If the verification fails due to incorrect OTP or other errors,
     *         the response includes an appropriate error message and HTTP status code.
     */
//    @GetMapping("/verification-transaction/{otp}/{transactionId}")
//    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
//    @Transactional
//    public ResponseEntity<Response> otpVerificationTransaction(@PathVariable String otp,
//                                                         @PathVariable Long transactionId) {
//        try {
//            boolean isVerified = mailSenderService.verifyOtpForTransaction(otp, transactionId);
//            if (!isVerified) {
//                Response response = new Response(HttpStatus.NOT_FOUND, "OTP :" + otp + " is incorrect! Try again");
//                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
//            }
//            if (otp == null || otp.isEmpty()) {
//                throw new NotFoundException("OTP is null or empty");
//            }
//            Response response = new Response(HttpStatus.OK, "Transaction Verified!", true);
//            return new ResponseEntity<>(response, HttpStatus.OK);
//        } catch (Exception e) {
//            Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), false);
//            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }


    /**
     * Resends the OTP for a transaction.
     * This endpoint is secured with Spring Security, allowing only users with the 'ROLE_CUSTOMER' role to access it.
     * It looks up the transaction by its ID and triggers the OTP generation process for that transaction again.
     * If the transaction is found and the OTP is successfully resent, it returns a success response.
     * If the transaction cannot be found or any other error occurs, it returns an error response.
     *
     * @param transactionId The ID of the transaction for which the OTP needs to be resent.
     * @return A {@link ResponseEntity} containing a {@link Response} object. The response includes a success message
     *         and a status of OK if the OTP is resent successfully. In case of failure, it includes an appropriate
     *         error message and HTTP status code.
     */
//    @GetMapping("/resent-otp-transaction/{transactionId}")
//    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
//    @Transactional
//    public ResponseEntity<Response> resentOtpTransaction(@PathVariable Long transactionId) {
//        try {
//            GoldTransaction transaction = goldTransactionRepository.findById(transactionId)
//                    .orElseThrow(() -> new NotFoundException("Transaction not found with ID: " + transactionId));
//            mailSenderService.generateOtpForTransaction(transaction);
//            Response response = new Response(HttpStatus.OK, "OTP has been resent successfully.", true);
//            return new ResponseEntity<>(response, HttpStatus.OK);
//        } catch (Exception e) {
//            Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), false);
//            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }


    /**
     * Accepts a transaction for mobile clients based on the provided transaction ID, optional digital signature, and public key.
     * This endpoint is part of a larger workflow involving transaction verification and processing, where the digital signature
     * serves as an additional layer of security or verification if provided. The public key is essential for authentication
     * or authorization purposes to accept the transaction. This method is secured with Spring Security, allowing only users
     * with the 'ROLE_CUSTOMER' role to access it.
     *
     * @param transactionId The ID of the transaction to be accepted. It is passed as a path variable.
     * @param signatureImage An optional multipart file containing a digital signature associated with the transaction.
     *                       This parameter is not mandatory and can be null.
     * @param publicKey A string representing a public key used for authentication or authorization to accept the transaction.
     *                  It is passed as a request parameter.
     * @return A {@link ResponseEntity} containing a {@link Response} object. The response includes a success message
     *         and the details of the accepted transaction if the operation is successful. In case of failure, it includes
     *         an appropriate error message and HTTP status code indicating the nature of the failure (e.g., NOT_FOUND,
     *         BAD_REQUEST, INTERNAL_SERVER_ERROR).
     */
    @PutMapping("/transactions-accepted/mobile/{transactionId}")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<Response> transactionAcceptedForMobile(@PathVariable Long transactionId,
                                                        @RequestParam(value = "signatureImage", required = false) MultipartFile signatureImage,
                                                        @RequestParam(value = "publicKey") String publicKey) {
        try {
            TransactionDTO transactionDTO = transactionServices.transactionAcceptedForMobile(transactionId, signatureImage, publicKey);
            Response response = new Response(HttpStatus.OK, "Transaction has been accepted", transactionDTO);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (NotFoundException e) {
            Response response = new Response(HttpStatus.NOT_FOUND, e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (IllegalStateException e) {
            Response response = new Response(HttpStatus.BAD_REQUEST, e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null);
            return ResponseEntity.internalServerError().body(response);
        }
    }


    /**
     * Accepts a transaction for web-based clients.
     * This endpoint is secured with Spring Security, allowing only users with the 'ROLE_CUSTOMER' role to access it.
     * It accepts a transaction based on the provided transaction ID and public key. The method attempts to mark the transaction
     * as accepted through the {@link TransactionServices#transactionAcceptedForWeb} method and returns a response entity
     * containing the details of the accepted transaction if successful. In case of failure, it returns an appropriate error
     * message and HTTP status code.
     *
     * @param transactionId The ID of the transaction to be accepted.
     * @param publicKey A string representing a public key used for authentication or authorization to accept the transaction.
     * @return A {@link ResponseEntity} containing a {@link Response} object. The response includes a success message
     *         and the details of the accepted transaction if the operation is successful. In case of failure, it includes
     *         an appropriate error message and HTTP status code, such as NOT_FOUND for a {@link NotFoundException},
     *         BAD_REQUEST for an {@link IllegalStateException}, or INTERNAL_SERVER_ERROR for any other exceptions.
     */
    @PutMapping("/transactions-accepted/web/{transactionId}")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<Response> transactionAcceptedForWeb(@PathVariable Long transactionId,
                                                                 @RequestParam(value = "publicKey") String publicKey) {
        try {
            TransactionDTO transactionDTO = transactionServices.transactionAcceptedForWeb(transactionId, publicKey);
            Response response = new Response(HttpStatus.OK, "Transaction has been accepted", transactionDTO);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (NotFoundException e) {
            Response response = new Response(HttpStatus.NOT_FOUND, e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (IllegalStateException e) {
            Response response = new Response(HttpStatus.BAD_REQUEST, e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null);
            return ResponseEntity.internalServerError().body(response);
        }
    }


    /**
     * Updates the digital signature image for a transaction on mobile platforms.
     * This endpoint is secured with Spring Security, allowing only users with the 'ROLE_CUSTOMER' role to access it.
     * It updates the transaction's digital signature image and public key based on the provided transaction ID.
     *
     * @param transactionId The ID of the transaction for which the digital signature is to be updated.
     * @param signatureImage The multipart file containing the new digital signature image.
     * @param publicKey The public key associated with the digital signature, used for verification purposes.
     * @return A {@link ResponseEntity} containing a {@link Response} object. The response includes a success message
     *         and the details of the updated transaction if the operation is successful. In case of failure, it includes
     *         an appropriate error message and HTTP status code, such as NOT_FOUND for a {@link NotFoundException},
     *         BAD_REQUEST for an {@link IllegalStateException}, or INTERNAL_SERVER_ERROR for any other exceptions.
     */
    @PutMapping("/transactions-update/signature-image/mobile/{transactionId}")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<Response> updateTransactionSignatureForMobile(@PathVariable Long transactionId,
                                                              @RequestParam(value = "signature") MultipartFile signatureImage,
                                                              @RequestParam(value = "publicKey") String publicKey) {
        try {
            TransactionDTO transactionDTO = transactionServices.updateTransactionSignatureForMobile(transactionId, signatureImage, publicKey);
            Response response = new Response(HttpStatus.OK, "Transaction has been accepted", transactionDTO);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (NotFoundException e) {
            Response response = new Response(HttpStatus.NOT_FOUND, e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (IllegalStateException e) {
            Response response = new Response(HttpStatus.BAD_REQUEST, e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null);
            return ResponseEntity.internalServerError().body(response);
        }
    }


    /**
     * Verifies the digital signature of a contract using the provided public and private keys.
     * This endpoint is secured and transactional, ensuring that the verification process is consistent and isolated.
     * It is designed to validate the authenticity and integrity of a contract by comparing the digital signature
     * against the public key of the signer and the contract details.
     *
     * @param contractId The ID of the contract to be verified. This is a path variable.
     * @param verifyContractRequest The request body containing the public and private keys necessary for verification.
     * @return A {@link ResponseEntity} object containing either a success message and the verified {@link ContractDTO},
     *         or an error message and the appropriate HTTP status code in case of failure. The HTTP status code is OK (200)
     *         if the verification is successful, or INTERNAL_SERVER_ERROR (500) if an unexpected error occurs.
     * @throws Exception if the verification process fails due to an error in the underlying verification logic or data access issues.
     */
    @PostMapping("/verify/contract/{contractId}")
    @Transactional
    public ResponseEntity<?> verifyContract(@PathVariable Long contractId, @RequestBody VerifyContractRequest verifyContractRequest) throws Exception {
        ContractDTO contractDTO = transactionServices.verifyContract(contractId, verifyContractRequest.getPublicKey(), verifyContractRequest.getPrivateKey());
        return new Response(HttpStatus.OK, "Verify contract success", contractDTO).getResponseEntity();
    }

    /**
     * Handles a request to withdraw gold by a customer.
     * This endpoint is secured with Spring Security, allowing only users with the 'ROLE_CUSTOMER' role to access it.
     * It processes a withdrawal request by invoking the {@link TransactionServices#requestWithdrawal} method with the
     * provided user ID, weight to withdraw, and the unit of gold. The method returns a response entity containing
     * the details of the withdrawal request if successful, or an error message if the request fails due to
     * a {@link WithdrawalNotAllowedException} or any other exception.
     *
     * @param userInfoId The ID of the user requesting the gold withdrawal.
     * @param weightToWithdraw The amount of gold the user wishes to withdraw, specified in the unit provided.
     * @param unit The unit of gold to be withdrawn (e.g., OUNCE, GRAM).
     * @return A {@link ResponseEntity} containing a {@link Response} object. The response includes a success message
     *         and the details of the withdrawal request if the operation is successful. In case of failure, it includes
     *         an appropriate error message and HTTP status code, such as BAD_REQUEST for a {@link WithdrawalNotAllowedException}
     *         or INTERNAL_SERVER_ERROR for any other exceptions.
     */
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @PostMapping("/request-withdraw-gold/{userInfoId}")
    public ResponseEntity<Response> requestWithdrawGold(@Valid @PathVariable Long userInfoId,
                                                        @RequestParam(required = false) BigDecimal weightToWithdraw,
                                                        @RequestParam(required = false) GoldUnit unit,
                                                        @RequestParam WithdrawRequirement withdrawRequirement,
                                                        @RequestParam Long productId) {
        try {
            WithdrawGoldDefault withdrawGoldDTO = transactionServices.requestWithdrawal(userInfoId, weightToWithdraw, unit, withdrawRequirement, productId);
            Response response = new Response(HttpStatus.CREATED,
                    "Withdrawal request is being processed.", withdrawGoldDTO);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (WithdrawalNotAllowedException e) {
            Response response = new Response(HttpStatus.BAD_REQUEST, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Resends the OTP for a gold withdrawal request.
     * This endpoint is secured with Spring Security, allowing only users with the 'ROLE_CUSTOMER' role to access it.
     * It looks up the withdrawal request by its ID and triggers the OTP generation process for that withdrawal again.
     * If the withdrawal request is found and the OTP is successfully resent, it returns a success response.
     * If the withdrawal request cannot be found or any other error occurs, it returns an error response.
     *
     * @param withdrawId The ID of the withdrawal request for which the OTP needs to be resent.
     * @return A {@link ResponseEntity} containing a {@link Response} object. The response includes a success message
     *         and a status of OK if the OTP is resent successfully. In case of failure, it includes an appropriate
     *         error message and HTTP status code.
     */
    @GetMapping("/resent-otp-withdraw/{withdrawId}")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @Transactional
    public ResponseEntity<Response> resentOtpWithdraw(@PathVariable Long withdrawId) {
        try {
            WithdrawGold withdrawGold = withdrawGoldRepository.findById(withdrawId)
                    .orElseThrow(() -> new NotFoundException("Transaction not found with ID: " + withdrawId));
            mailSenderService.generateOtpForWithDraw(withdrawGold);
            Response response = new Response(HttpStatus.OK, "OTP has been resent successfully.", true);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), false);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Verifies the OTP for a gold withdrawal request.
     * This endpoint is secured with Spring Security, allowing only users with the 'ROLE_CUSTOMER' role to access it.
     * It checks if the provided OTP matches the one generated for the withdrawal request. If the OTP is correct,
     * the withdrawal request is marked as verified. If the OTP is incorrect or not found, it returns an error response.
     *
     * @param otp The one-time password provided by the user for withdrawal verification.
     * @param withdrawId The ID of the withdrawal request to be verified.
     * @return A {@link ResponseEntity} containing a {@link Response} object. If the OTP verification is successful,
     *         the response includes a success message. If the verification fails due to incorrect OTP or other errors,
     *         the response includes an appropriate error message and HTTP status code.
     */
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @PostMapping("/request-withdraw-gold/verify/{userInfoId}/{otp}/{withdrawId}")
    public ResponseEntity<Response> verifyWithdrawGold(@PathVariable Long userInfoId ,@PathVariable String otp,
                                                        @PathVariable Long withdrawId) {
        try {
            boolean isVerified = mailSenderService.verifyOtpForWithdraw(userInfoId, otp, withdrawId);
            if (!isVerified) {
                Response response = new Response(HttpStatus.NOT_FOUND, "OTP is incorrect! Please try again");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            if (otp == null || otp.isEmpty()) {
                throw new NotFoundException("OTP is null or empty");
            }
            Response response = new Response(HttpStatus.OK, "Transaction Verified!", true);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), false);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Handles the processing of a withdrawal request based on the provided withdrawal ID, action string, and QR code.
     * This endpoint is secured with Spring Security, allowing only users with 'ROLE_ADMIN' or 'ROLE_STAFF' roles to access it.
     * It attempts to process a withdrawal action (e.g., approve, reject) through the {@link TransactionServices#handleWithdrawal} method
     * and returns a response entity containing the details of the withdrawal action if successful, or an error message if the action fails.
     *
     * @param withdrawalId The ID of the withdrawal request to be processed.
     * @param actionStr The action to be taken on the withdrawal request, such as 'APPROVE' or 'REJECT'.
     * @param withdrawQrCode The QR code associated with the withdrawal request, used for verification or additional processing.
     * @return A {@link ResponseEntity} containing a {@link Response} object. The response includes a success message
     *         and the details of the processed withdrawal action if the operation is successful. In case of failure,
     *         it includes an appropriate error message and HTTP status code, such as NOT_ACCEPTABLE for a
     *         {@link WithdrawalNotAllowedException}, NOT_FOUND if the withdrawal request is not found, or
     *         INTERNAL_SERVER_ERROR for any other exceptions.
     */
    @PreAuthorize("hasRole('ROLE_ADMIN') " +
            "or hasRole('ROLE_STAFF')")
    @PatchMapping("/handle-withdraw-gold/{withdrawalId}")
    public ResponseEntity<Response> handleWithdrawal(@Valid @PathVariable Long withdrawalId,
                                                     @RequestParam String actionStr,
                                                     @RequestParam String withdrawQrCode) {
        try {
            WithdrawGoldDefault withdrawGoldDTO = transactionServices.handleWithdrawal(withdrawalId, actionStr, withdrawQrCode);
            Response response = new Response(HttpStatus.ACCEPTED,
                    "Withdrawal process is done.", withdrawGoldDTO);
            return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
        } catch (WithdrawalNotAllowedException e) {
            Response response = new Response(HttpStatus.NOT_ACCEPTABLE, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
        } catch (NotFoundException e) {
            Response response = new Response(HttpStatus.NOT_FOUND, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Cancels a withdrawal request for a given withdrawal ID.
     * This endpoint is secured with Spring Security, allowing access only to users with 'ROLE_CUSTOMER', 'ROLE_STAFF', or 'ROLE_ADMIN' roles.
     * It attempts to cancel a withdrawal request by invoking the {@link TransactionServices#cancelWithdrawal} method with the
     * provided withdrawal ID, cancellation reason, and the username of the authenticated user. The method returns a response entity
     * containing the details of the cancelled withdrawal request if successful, or an error message if the request fails due to
     * a {@link NotFoundException}, {@link WithdrawalNotAllowedException}, or any other exception.
     *
     * @param withdrawalId The ID of the withdrawal request to be cancelled.
     * @param reason The reason for cancelling the withdrawal request.
     * @return A {@link ResponseEntity} containing a {@link Response} object. The response includes a success message
     *         and the details of the cancelled withdrawal request if the operation is successful. In case of failure,
     *         it includes an appropriate error message and HTTP status code, such as NOT_FOUND for a {@link NotFoundException},
     *         NOT_ACCEPTABLE for a {@link WithdrawalNotAllowedException}, or INTERNAL_SERVER_ERROR for any other exceptions.
     */
    @PreAuthorize("hasRole('ROLE_CUSTOMER') " +
            "or hasRole('ROLE_STAFF') " +
            "or hasRole('ROLE_ADMIN')")
    @PostMapping("/cancel/{withdrawalId}")
    public ResponseEntity<Response> cancelWithdrawal(@PathVariable Long withdrawalId, @RequestParam String reason) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            WithdrawGoldDTO cancelledWithdrawal = transactionServices.cancelWithdrawal(withdrawalId, reason, username);
            Response response = new Response(HttpStatus.ACCEPTED,
                    "Cancelled request sent!", cancelledWithdrawal);
            return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
        } catch (NotFoundException e) {
            Response response = new Response(HttpStatus.NOT_FOUND, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (WithdrawalNotAllowedException e) {
            Response response = new Response(HttpStatus.NOT_ACCEPTABLE, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
        } catch (Exception e) {
            Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Retrieves a list of all transactions.
     * This endpoint is secured with Spring Security, requiring the user to have the 'ROLE_ADMIN' role.
     * It fetches a list of all transactions using the {@link TransactionServices#transactionList} method.
     * If the operation is successful, it returns a response entity containing the list of transactions
     * and an HTTP status code of OK. In case of any exceptions, it returns an error message and an HTTP
     * status code indicating an internal server error.
     *
     * @return A {@link ResponseEntity} object containing either a list of {@link TransactionDTO} objects
     *         and an HTTP status code of OK, or an error message and an HTTP status code indicating an internal server error.
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/transaction-list")
    public ResponseEntity<?> transactionList() {
        try {
            List<TransactionDTO> transactionList = transactionServices.transactionList();
            Response response = new Response(HttpStatus.OK,
                    "All List Transaction", transactionList);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Retrieves a list of transactions for a specific user.
     * This endpoint is secured with Spring Security and checks if the request is made by the user themselves or by an admin/staff member.
     * If the request does not meet these conditions, it returns a forbidden status. Otherwise, it fetches and returns the list of transactions
     * associated with the given user ID.
     *
     * @param userInfoId The ID of the user whose transactions are to be retrieved.
     * @return A {@link ResponseEntity} containing a {@link Response} object with the list of {@link TransactionDTO} if successful,
     *         or an error message with the appropriate HTTP status code in case of failure. The HTTP status code is OK (200) if the
     *         transactions are retrieved successfully, FORBIDDEN (403) if the request is unauthorized, or INTERNAL_SERVER_ERROR (500)
     *         if an unexpected error occurs.
     * @throws IllegalAccessException if the authentication process fails.
     */
    @GetMapping("/user-transaction-list/{userInfoId}")
    @PreAuthorize(Constants.PREAUTHORIZE_ALL_ROLE)
    public ResponseEntity<Response> transactionListUser(@PathVariable Long userInfoId) throws IllegalAccessException {
        Authentication authentication = AuthenticationHelper.getAuthentication();
        boolean isForMe = AuthenticationHelper.isSameUserId(authentication, userInfoId);
        boolean isAdminOrStaff = AuthenticationHelper.isAdminOrStaff(authentication);
        if (!isForMe && !isAdminOrStaff) {
            return new Response(HttpStatus.FORBIDDEN, "Method Not Allowed").getResponseEntity();
        }
        try {
            List<TransactionDTO> transactionDTO = transactionServices.transactionListUser(userInfoId);
            Response response = new Response(HttpStatus.OK, "Withdraws", transactionDTO);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Retrieves a list of all withdrawal requests.
     * This endpoint is secured with Spring Security, requiring the user to have either the 'ROLE_ADMIN' or 'ROLE_STAFF' role.
     * It fetches a list of all withdrawal requests using the {@link TransactionServices#withdraws} method.
     * If the operation is successful, it returns a response entity containing the list of withdrawal requests
     * and an HTTP status code of OK. In case of any exceptions, it returns an error message and an HTTP
     * status code indicating an internal server error.
     *
     * @return A {@link ResponseEntity} object containing either a list of {@link WithdrawGoldDTO} objects
     *         and an HTTP status code of OK, or an error message and an HTTP status code indicating an internal server error.
     */
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STAFF')")
    @GetMapping("/withdraw-list")
    public ResponseEntity<Response> withdraws() {
        try {
            List<WithdrawGoldDTO> withdraws = transactionServices.withdraws();
            Response response = new Response(HttpStatus.OK, "Withdraws", withdraws);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a list of all withdrawal requests for a specific user.
     * This endpoint is secured with Spring Security and checks if the request is made by the user themselves or by an admin/staff member.
     * If the request does not meet these conditions, it returns a forbidden status. Otherwise, it fetches and returns the list of withdrawal
     * requests associated with the given user ID.
     *
     * @param userInfoId The ID of the user whose withdrawal requests are to be retrieved.
     * @return A {@link ResponseEntity} containing a {@link Response} object with the list of {@link WithdrawGoldDTO} if successful,
     *         or an error message with the appropriate HTTP status code in case of failure. The HTTP status code is OK (200) if the
     *         withdrawal requests are retrieved successfully, FORBIDDEN (403) if the request is unauthorized, or INTERNAL_SERVER_ERROR (500)
     *         if an unexpected error occurs.
     * @throws IllegalAccessException if the authentication process fails.
     */
    @GetMapping("/withdraw-list-userinfo/{userInfoId}")
    @PreAuthorize(Constants.PREAUTHORIZE_ALL_ROLE)
    public ResponseEntity<Response> withdraws(@PathVariable long userInfoId) throws IllegalAccessException {
        Authentication authentication = AuthenticationHelper.getAuthentication();
        boolean isForMe = AuthenticationHelper.isSameUserId(authentication, userInfoId);
        boolean isAdminOrStaff = AuthenticationHelper.isAdminOrStaff(authentication);
        if (!isForMe && !isAdminOrStaff) {
            return new Response(HttpStatus.FORBIDDEN, "Method Not Allowed").getResponseEntity();
        }
        try {
            List<WithdrawGoldDTO> withdraws = transactionServices.withdrawsUserInfo(userInfoId);
            Response response = new Response(HttpStatus.OK, "Withdraws", withdraws);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    @GetMapping("/withdraw-with-code")
//    @PreAuthorize("hasRole('ROLE_STAFF') or hasRole('ROLE_ADMIN')")
//    @Transactional
//    public ResponseEntity<Response> withdraws(@RequestParam String qrCode) {
//        if (qrCode == null) qrCode = "";
//        try {
//            WithdrawGoldDTO withdraw = transactionServices.getWithdrawQrCode(qrCode);
//            if (withdraw != null) {
//                transactionServices.handleWithdrawal(withdraw.getId(), "CONFIRM_WITHDRAWAL", qrCode);
//            }
//            return new Response(HttpStatus.OK, "Get withdraw information success", withdraw).getResponseEntity();
//        } catch (Exception e) {
//            return Response.getResponseFromException(e);
//        }
//    }


    /**
     * Retrieves a list of all contracts from the database.
     * This method fetches all contracts using the {@link ContractRepository#findAll} method,
     * then maps each contract entity to a {@link ContractDTO} object to abstract and simplify the data for client-side usage.
     * The mapping includes details such as the contract's ID, action party, full name, address, quantity of gold transacted,
     * price per ounce, total cost or profit, transaction type, creation date, confirming party, gold unit, and signatures from both parties.
     *
     * @return A list of {@link ContractDTO} objects representing all contracts in the database. Each {@link ContractDTO} contains
     *         detailed information about a contract, structured in a way that is suitable for client-side rendering or processing.
     */
    @GetMapping("/get-all/contract")
    public List<ContractDTO> getAllContract() {
        return contractRepository.findAll().stream().map(contract -> {
            ContractDTO dto = new ContractDTO();
            dto.setId(contract.getId());
            dto.setActionParty(contract.getActionParty().getId());
            dto.setFullName(contract.getActionPartyFullName());
            dto.setAddress(contract.getActionPartyAddress());
            dto.setQuantity(contract.getQuantity());
            dto.setPricePerOunce(contract.getPricePerOunce());
            dto.setTotalCostOrProfit(contract.getTotalCostOrProfit());
            dto.setTransactionType(contract.getTransactionType());
            dto.setCreatedAt(contract.getCreatedAt());
            dto.setConfirmingParty(contract.getConfirmingParty());
            dto.setGoldUnit(contract.getGoldUnit());
            dto.setSignatureActionParty(contract.getSignatureActionParty());
            dto.setSignatureConfirmingParty(contract.getSignatureConfirmingParty());
            return dto;
        }).collect(Collectors.toList());
    }


    @GetMapping("/convert/price-per-ounce")
    public ResponseEntity<Response> convertPricePerOunce(@RequestParam GoldUnit goldUnit, @RequestParam BigDecimal pricePerOz,
                                                         @RequestParam BigDecimal quantityInOz) {
        try {

            BigDecimal convertQuantityInOz = goldUnit.convertToTroyOunce(quantityInOz);
            BigDecimal totalAmount = convertQuantityInOz.multiply(pricePerOz);
            Response response = new Response(HttpStatus.OK, "Convert price per ounce success", totalAmount);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/user-confirm-withdraw/{withdrawId}")
    public ResponseEntity<Response> userConfirmReceived(@PathVariable Long withdrawId){
       try{
           boolean check = transactionServices.userConfirmReceived(withdrawId);
           Response response = new Response(HttpStatus.OK, "User confirm received gold from request withdraw success.", check);
           return new ResponseEntity<>(response, HttpStatus.OK);
       } catch (UserConfirmWithdrawException e) {
           Response response = new Response(HttpStatus.BAD_REQUEST, e.getMessage(), null);
           return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
       } catch (NotFoundException e) {
           Response response = new Response(HttpStatus.NOT_FOUND, e.getMessage(), null);
           return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
       } catch (Exception e) {
           Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null);
           return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
       }
    }
}
