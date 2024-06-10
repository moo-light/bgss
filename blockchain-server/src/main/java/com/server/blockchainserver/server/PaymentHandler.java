package com.server.blockchainserver.server;

import com.server.blockchainserver.dto.balance_dto.BalanceDTO;
import com.server.blockchainserver.dto.payment_dto.PaymentHistoryDTO;
import com.server.blockchainserver.exeptions.NotFoundException;
import com.server.blockchainserver.models.enums.PaymentStatus;
import com.server.blockchainserver.models.user_model.Balance;
import com.server.blockchainserver.models.user_model.PaymentHistory;
import com.server.blockchainserver.models.user_model.UserInfo;
import com.server.blockchainserver.platform.repositories.BalanceRepository;
import com.server.blockchainserver.platform.repositories.PaymentHistoryRepository;
import com.server.blockchainserver.repository.user_repository.UserInfoRepository;
import jakarta.transaction.Transactional;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

@Component
public class PaymentHandler {

    @Autowired
    BalanceRepository balanceRepository;

    @Autowired
    PaymentHistoryRepository paymentHistoryRepository;

    @Autowired
    UserInfoRepository userInfoRepository;

    @Transactional
    public PaymentHistoryDTO recordSuccessfulPayment(Long userInfoId, String orderCode, BigDecimal amount, String bankCode,
                                                     PaymentStatus paymentStatus, Instant payDate, String reason) {
        // Tìm UserInfo thông qua userInfoId.
        UserInfo userInfo = userInfoRepository.findById(userInfoId).orElseThrow(
                () -> new RuntimeException("UserInfo ID: " + userInfoId + " not found")
        );

        Balance balance = balanceRepository.findBalanceByUserInfoId(userInfoId).orElseThrow();
        // Tạo và lưu PaymentHistory mới.
        PaymentHistory paymentHistory = new PaymentHistory();
        paymentHistory.setUserInfo(userInfo);
        paymentHistory.setBankCode(bankCode);
        paymentHistory.setAmount(amount.setScale(2, RoundingMode.HALF_UP));
        // Tham chiếu độc nhất cho giao dịch.
        paymentHistory.setOrderCode(orderCode);
        paymentHistory.setPayDate(payDate);
        paymentHistory.setPaymentStatus(paymentStatus);
        paymentHistory.setReason(reason);
        if (PaymentStatus.SUCCESS.equals(paymentStatus)) {
            updateBalance(userInfo, amount);
        }
        paymentHistoryRepository.save(paymentHistory);

        return new PaymentHistoryDTO(paymentHistory.getId(), paymentHistory.getOrderCode(),
                paymentHistory.getAmount(), paymentHistory.getBankCode(), paymentHistory.getPaymentStatus(),
                paymentHistory.getPayDate(), paymentHistory.getReason(),
                paymentHistory.getUserInfo().getId(), balance.getBalance());
    }

    private void updateBalance(UserInfo userInfo, BigDecimal amount) {
        // Ví dụ: Tìm Balance liên quan đến UserInfo và cập nhật số tiền.
        Balance userInfoBalance = balanceRepository.findBalanceByUserInfoId(userInfo.getId()).orElseThrow(
                () -> new RuntimeException("Balance not found")
        );
        userInfoBalance.setBalance(userInfoBalance.getBalance().add(amount).setScale(2, RoundingMode.HALF_UP));
        balanceRepository.save(userInfoBalance);
    }

    public BigDecimal convertCurrency(BigDecimal amount) throws Exception {
        String from = "VND";
        String to = "USD";
        String apiKey = "528083a3b1da52460eff5f293e9754e58b3c78d3";
        String url = String.format(
                "https://api.getgeoapi.com/v2/currency/convert?api_key=%s&from=%s&to=%s&amount=%s&format=json",
                apiKey, from, to, amount
        );

        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(new HttpGet(url))) {
            if (response.getCode() == 200) {
                String jsonResult = EntityUtils.toString(response.getEntity());
                JSONObject jsonObject = new JSONObject(jsonResult);

                // Lấy đối tượng 'rates' từ JSON
                JSONObject rates = jsonObject.getJSONObject("rates");
                JSONObject rateInfo = rates.getJSONObject(to);

                // Lấy giá trị 'rate_for_amount'
                BigDecimal rateForAmount = rateInfo.getBigDecimal("rate_for_amount");

                return rateForAmount;
            } else {
                throw new IOException("Unexpected response status: " + response.getCode());
            }
        }
    }

    public BalanceDTO balance(long userInfoId) {
        Balance balance = balanceRepository.findBalanceByUserInfoId(userInfoId)
                .orElseThrow(() -> new NotFoundException("User Info Id: " + userInfoId + " Not found"));
        return new BalanceDTO(balance.getId(), balance.getBalance(), balance.getUserInfo().getId());
    }
}
