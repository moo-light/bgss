package com.server.blockchainserver.services;

import com.server.blockchainserver.models.shopping_model.Order;
import com.server.blockchainserver.models.user_model.User;
import com.server.blockchainserver.platform.entity.GoldTransaction;
import com.server.blockchainserver.platform.entity.WithdrawGold;
import com.server.blockchainserver.server.MailAuthentication;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class MailSenderImpl  implements  MailSenderService{


    @Autowired
    MailAuthentication mailAuthentication;
    @Autowired
    private static final Logger logger = LoggerFactory.getLogger(MailSenderImpl.class);

    @Override
    @Async
    public void mailSender(User user) throws MessagingException, IOException {
        mailAuthentication.sendVerificationEmail(user);
    }

//    @Override
//    public void sendVerificationOtpOrder(Order order, String otp) {
//        mailAuthentication.sendVerificationOtpOrder(order, otp);
//    }

    @Override
    @Async
    public void generateAndSendOtp(Order order) {
        try {
            String otp = mailAuthentication.generateOtpForOrder(order);
            mailAuthentication.sendVerificationOtpOrder(order, otp);
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Error sending OTP email: " + e.getMessage());
        }
    }

    @Override
    public boolean verifyOtpForOrder(String otp, Long orderId) {
        return mailAuthentication.verifyOtpForOrder(otp, orderId);
    }

    @Override
    public void generateOtpForOrder(Order order) throws MessagingException, IOException {
        mailAuthentication.generateOtpForOrder(order);
    }

//    @Override
//    public boolean verifyOtpForTransaction(String otp, Long transactionId) {
//        return mailAuthentication.verifyOtpForTransaction(otp, transactionId);
//    }
//
//    @Override
//    public void generateOtpForTransaction(GoldTransaction transaction) throws MessagingException, IOException {
//        mailAuthentication.generateOtpForTransaction(transaction);
//    }

    @Override
    public boolean verifyOtpForWithdraw(Long userInfoId ,String otp, Long withdrawId) {
        return mailAuthentication.verifyOtpForWithdrawal(userInfoId ,otp, withdrawId);
    }

    @Override
    public void generateOtpForWithDraw(WithdrawGold withdrawGold) throws MessagingException, IOException {
        mailAuthentication.generateOtpForWithdraw(withdrawGold);
    }

}
