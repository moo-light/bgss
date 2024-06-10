package com.server.blockchainserver.controllers.payment_controller;

import com.server.blockchainserver.advices.Constants;
import com.server.blockchainserver.advices.Response;
import com.server.blockchainserver.dto.balance_dto.BalanceDTO;
import com.server.blockchainserver.dto.payment_dto.PaymentHistoryDTO;
import com.server.blockchainserver.models.enums.PaymentStatus;
import com.server.blockchainserver.models.user_model.Balance;
import com.server.blockchainserver.models.user_model.PaymentHistory;
import com.server.blockchainserver.platform.platform_services.services_interface.WalletService;
import com.server.blockchainserver.platform.repositories.BalanceRepository;
import com.server.blockchainserver.platform.repositories.PaymentHistoryRepository;
import com.server.blockchainserver.platform.scheduling.ScheduledTasks;
import com.server.blockchainserver.server.PaymentHandler;
import com.server.blockchainserver.services.Services;
import com.server.blockchainserver.vnpay.VNPayConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.websocket.server.PathParam;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class PaymentController {

    @Autowired
    BalanceRepository balanceRepository;

    @Autowired
    PaymentHandler paymentHandler;

    @Autowired
    WalletService walletService;

    @Autowired
    Services paymentHistoryService;

    @Autowired
    private PaymentHistoryRepository paymentHistoryRepository;

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @GetMapping("/check-valid-pay")
    public ResponseEntity<Response> checkValidPay(@RequestParam Map<String, String> queryParams) throws Exception {

        String vnp_ResponseCode = queryParams.get("vnp_ResponseCode");
        String userInfoIdStr = queryParams.get("vnp_OrderInfo");
        long userInfoId = Long.parseLong(userInfoIdStr);
        String amountStr = queryParams.get("vnp_Amount");
        BigDecimal amount = new BigDecimal(amountStr);
        String orderCodeStr = queryParams.get("vnp_TxnRef");
        String bankCodeStr = queryParams.get("vnp_BankCode");
        String payDayStr = queryParams.get("vnp_PayDate");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime localDateTime = LocalDateTime.parse(payDayStr, formatter);



        Instant payDate = localDateTime.toInstant(ZoneOffset.ofHours(7));

        BigDecimal convertAmount = paymentHandler.convertCurrency(amount.divide(BigDecimal.valueOf(100)));

        List<String> orderCode = paymentHistoryRepository.findOrderCodesByUserInfoId(userInfoId);
        if (orderCode != null && orderCode.equals(orderCodeStr)) {

                Response response = new Response(HttpStatus.NOT_ACCEPTABLE, "You have already deposited money", null);
                return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);

        }
        logger.info("Response code: {}", vnp_ResponseCode);
        logger.info("Order code: {}", orderCodeStr);
        logger.info("Amount: {}", convertAmount);
        logger.info("Bank code: {}", bankCodeStr);
        logger.info("Pay date: {}", payDate);
        logger.info("userId: {}", userInfoId);
        switch (vnp_ResponseCode) {
            case "00":
                try {

                    PaymentStatus paymentStatus = PaymentStatus.SUCCESS;

                    PaymentHistoryDTO paymentHistory = paymentHandler.recordSuccessfulPayment(
                            userInfoId, orderCodeStr, convertAmount, bankCodeStr, paymentStatus, payDate, Constants.Successfully_Transaction);
                    Response response = new Response(HttpStatus.OK, "Successful transaction", paymentHistory);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } catch (Exception e) {
                    Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null);
                    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            case "07":
                try {
                    PaymentStatus paymentStatus = PaymentStatus.SUCCESS;
                    PaymentHistoryDTO paymentHistory = paymentHandler.recordSuccessfulPayment(
                            userInfoId, orderCodeStr, convertAmount, bankCodeStr, paymentStatus, payDate, Constants.Successfully_Transaction);
                    Response response = new Response(HttpStatus.OK, "Successful transaction", paymentHistory);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } catch (Exception e) {
                    Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null);
                    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            case "09":
                try {
                    PaymentStatus paymentStatus = PaymentStatus.FAILED;
                    PaymentHistoryDTO paymentHistory = paymentHandler.recordSuccessfulPayment(
                            userInfoId, orderCodeStr, BigDecimal.ZERO, bankCodeStr, paymentStatus, payDate, Constants.Not_Registered_InternetBanking);
                    Response response = new Response(HttpStatus.FORBIDDEN, Constants.Not_Registered_InternetBanking, paymentHistory);
                    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
                } catch (Exception e) {
                    Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null);
                    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            case "10":
                try {
                    PaymentStatus paymentStatus = PaymentStatus.FAILED;
                    PaymentHistoryDTO paymentHistory = paymentHandler.recordSuccessfulPayment(
                            userInfoId, orderCodeStr, BigDecimal.ZERO, bankCodeStr, paymentStatus, payDate, Constants.Verify_Incorrect);
                    Response response = new Response(HttpStatus.FORBIDDEN, Constants.Verify_Incorrect, paymentHistory);
                    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
                } catch (Exception e) {
                    Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null);
                    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
                }
//            case "11":
//                return new Response(HttpStatus.NOT_ACCEPTABLE,
//                        "Transaction failed due to: Payment waiting period has expired. Please retry the transaction.",
//                        null);
//            case "12":
//                return new Response(HttpStatus.NOT_ACCEPTABLE, "Transaction failed due to: Customer's card/account is locked.",
//                        null);
//            case "13":
//                return new Response(HttpStatus.NOT_ACCEPTABLE,
//                        "The transaction was unsuccessful because you entered the wrong transaction authentication password (OTP). Please retry the transaction.",
//                        null);
            case "24":
                try {
                    PaymentStatus paymentStatus = PaymentStatus.CANCEL;
                    PaymentHistoryDTO paymentHistory = paymentHandler.recordSuccessfulPayment(
                            userInfoId, orderCodeStr, BigDecimal.ZERO, bankCodeStr, paymentStatus, payDate, Constants.Cancel_Transaction);
                    Response response = new Response(HttpStatus.FORBIDDEN, Constants.Cancel_Transaction, paymentHistory);
                    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
                } catch (Exception e) {
                    Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null);
                    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            case "51":
                try {
                    PaymentStatus paymentStatus = PaymentStatus.FAILED;
                    PaymentHistoryDTO paymentHistory = paymentHandler.recordSuccessfulPayment(
                            userInfoId, orderCodeStr, convertAmount, bankCodeStr, paymentStatus, payDate, Constants.Insufficient_Balance);
                    Response response = new Response(HttpStatus.FORBIDDEN, Constants.Insufficient_Balance, paymentHistory);
                    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
                } catch (Exception e) {
                    Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null);
                    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
                }
//            case "65":
//                return new Response(HttpStatus.NOT_ACCEPTABLE,
//                        "Transaction failed due to: Your account has exceeded the transaction limit for the day.",
//                        null);
//            case "75":
//                return new Response(HttpStatus.NOT_ACCEPTABLE, "The payment bank is under maintenance.", null);
//            case "79":
//                return new Response(HttpStatus.NOT_ACCEPTABLE,
//                        "Transaction failed due to: DO NOT enter the wrong payment password more than the specified number of times. Please retry the transaction",
//                        null);
//            case "99":
//                return new Response(HttpStatus.NOT_ACCEPTABLE, "An unknown error", null);
            default:
                Response response = new Response();
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/payment")
//    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public String getPay(@PathParam("price") long price,
                         @PathParam("userInfoId") String userInfoId,
                         @PathParam("username") String username, HttpServletRequest request)
            throws UnsupportedEncodingException {
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String orderType = "other";
        long amount = price * 100;
        String bankCode = "VNBANK";

        String vnp_TxnRef = VNPayConfig.getRandomNumber(8);
        String vnp_IpAddr = "127.0.0.1";

        String vnp_TmnCode = VNPayConfig.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();

        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");

        vnp_Params.put("vnp_BankCode", bankCode);
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo",  userInfoId);
        vnp_Params.put("vnp_OrderType", orderType);


        vnp_Params.put("vnp_Locale", "vn");
        // Be config host (tốn sức chỉnh)
//         vnp_Params.put("vnp_ReturnUrl", VNPayConfig.vnp_ReturnUrl + "?orderId=" + userInfoId);

        // Fe tự config host (vấn đề bảo mật?)
        // if (host.endsWith("/")) {
        //     host = host.substring(0, host.length() - 1);
        // }
        // vnp_Params.put("vnp_ReturnUrl", host + VNPayConfig.vnp_ReturnUrl + "?orderId=" + orderId);

        // Be lấy url của người dùng request (ảnh hưởng bảo mật?)
//        int port = request.getServerPort();

//        int port = 3000;
//        if (request.getScheme().equals("http") && port == 80) {
//            port = -1;
//        } else if (request.getScheme().equals("https") && port == 443) {
//            port = -1;
//        }
//        URL serverURL;
//        try {
//            serverURL = new URL(request.getScheme(), request.getServerName(), port, "");
//            vnp_Params.put("vnp_ReturnUrl", serverURL + VNPayConfig.vnp_ReturnUrl + "?orderId=" + userInfoId);
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
            vnp_Params.put("vnp_ReturnUrl", VNPayConfig.getReturnHost() + VNPayConfig.vnp_ReturnUrl + "?orderId=" + userInfoId);
//        }
//        vnp_Params.put("vnp_IpnUrl", VNPayConfig.getReturnHost() + VNPayConfig.vnp_ReturnUrl + "?orderId=" + userInfoId);

        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                // Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                // Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = VNPayConfig.vnp_PayUrl + "?" + queryUrl;
        return paymentUrl;
    }

    @GetMapping("/balance/{userInfoId}")
    public ResponseEntity<Response> balance(@PathVariable long userInfoId) {
        try {
            BalanceDTO balance = walletService.balance(userInfoId);
            Response response = new Response(HttpStatus.OK, "balance", balance);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/history-deposit/{userId}")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<Response> historyDepositOfUser(@PathVariable Long userId){
        try {
            List<PaymentHistoryDTO> histories = paymentHistoryService.historyDepositOfUser(userId);
            Response response = new Response(HttpStatus.OK, "Show history deposit of user success.", histories);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
