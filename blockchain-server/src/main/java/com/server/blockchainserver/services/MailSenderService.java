package com.server.blockchainserver.services;

import com.server.blockchainserver.models.shopping_model.Order;
import com.server.blockchainserver.models.user_model.User;
import com.server.blockchainserver.platform.entity.GoldTransaction;
import com.server.blockchainserver.platform.entity.WithdrawGold;
import jakarta.mail.MessagingException;

import java.io.IOException;

public interface MailSenderService {
    void mailSender(User user) throws MessagingException, IOException;

//    void sendVerificationOtpOrder(Order order, String otp);

    void generateAndSendOtp(Order order);

    boolean verifyOtpForOrder(String otp, Long orderId);

    void generateOtpForOrder(Order order) throws MessagingException, IOException;


//    boolean verifyOtpForTransaction(String otp, Long transactionId);
//
//    void generateOtpForTransaction(GoldTransaction transaction) throws MessagingException, IOException;

    boolean verifyOtpForWithdraw(Long userInfoId ,String otp, Long withdrawId);

    void generateOtpForWithDraw(WithdrawGold withdrawGold) throws MessagingException, IOException;
}
