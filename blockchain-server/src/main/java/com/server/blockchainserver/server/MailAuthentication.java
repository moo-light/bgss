package com.server.blockchainserver.server;

import com.server.blockchainserver.exeptions.*;
import com.server.blockchainserver.gmail.OtpVerification;
import com.server.blockchainserver.models.enums.EGoldOptionType;
import com.server.blockchainserver.models.enums.EReceivedStatus;
import com.server.blockchainserver.models.shopping_model.Order;
import com.server.blockchainserver.models.shopping_model.OrderDetail;
import com.server.blockchainserver.models.shopping_model.Product;
import com.server.blockchainserver.models.user_model.Balance;
import com.server.blockchainserver.models.user_model.User;
import com.server.blockchainserver.models.user_model.VerificationToken;
import com.server.blockchainserver.platform.entity.Contract;
import com.server.blockchainserver.platform.entity.GoldTransaction;
import com.server.blockchainserver.platform.entity.UserGoldInventory;
import com.server.blockchainserver.platform.entity.WithdrawGold;
import com.server.blockchainserver.platform.entity.enums.GoldUnit;
import com.server.blockchainserver.platform.entity.enums.TransactionVerification;
import com.server.blockchainserver.platform.entity.enums.WithdrawalStatus;
import com.server.blockchainserver.platform.repositories.BalanceRepository;
import com.server.blockchainserver.platform.repositories.GoldTransactionRepository;
import com.server.blockchainserver.platform.repositories.UserGoldInventoryRepository;
import com.server.blockchainserver.platform.repositories.WithdrawGoldRepository;
import com.server.blockchainserver.repository.otp_verification.OtpVerificationRepository;
import com.server.blockchainserver.repository.shopping_repository.OrderDetailRepository;
import com.server.blockchainserver.repository.shopping_repository.OrderRepository;
import com.server.blockchainserver.repository.shopping_repository.ProductRepository;
import com.server.blockchainserver.repository.user_repository.VerificationTokenRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;


/**
 * This class is responsible for sending verification emails and handling OTP verifications.
 * It uses the '{@link MimeMessage}' class to create and send emails with custom HTML templates.
 * The class also provides methods for generating OTPs and deducting amounts from user balances.
 * @author BGSS Company
 */
@Component
public class MailAuthentication {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
    @Autowired
    private OtpVerificationRepository otpVerificationRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private BalanceRepository balanceRepository;
    @Autowired
    private GoldTransactionRepository goldTransactionRepository;
    @Autowired
    private WithdrawGoldRepository withdrawGoldRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserGoldInventoryRepository userInventoryRepository;

    @Value("${blockchain.openapi.dev-url}")
    private String openApiDevUrl;

    @Value("${blockchain.openapi.prod-url}")
    private String openApiProdUrl;




    /**
     * Loads the specified HTML template from the classpath resources.
     *
     * @param fileName The name of the HTML template file.
     * @return The content of the HTML template file as a string.
     * @throws IOException If there is an error in reading the HTML template file.
     */
    private String loadEmailHtmlTemplate(String fileName) throws IOException {
        InputStream inputStream = new ClassPathResource("templates/" + fileName).getInputStream();
        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }

    /**
     * Loads the specified HTML template from the resources' folder.
     * 'Templates' The name of the HTML template to be loaded.
     * @return The content of the HTML template as a string.
     * @throws IOException If there is an error in reading the HTML template file.
     */
    private String loadEmailHtmlTemplateOtp() throws IOException {
        InputStream inputStream = new ClassPathResource("templates/sendOTPVerification.html").getInputStream();
        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }


    /**
     * Sends a verification email to the specified user.
     *
     * @param user The {@link User} object representing the user for whom the verification email is being sent.
     * @throws MessagingException If there is a failure in creating or sending the email.
     * @throws IOException If there is an error in loading the HTML template or reading the logo image file.
     */
    public void sendVerificationEmail(User user) throws MessagingException, IOException {
        String token = generateVerificationToken(user);
        String recipientAddress = user.getEmail();
        String subject = "Verification";
        String confirmationUrl
                = openApiProdUrl + "/api/auth/verify?token=" + token;

        String htmlTemplate = loadEmailHtmlTemplate("sendEmailVerification.html");
        String htmlMsg = htmlTemplate.replace("${confirmationUrl}", confirmationUrl);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");


        helper.setTo(recipientAddress);
        helper.setSubject(subject);

        helper.setFrom(new InternetAddress("no-reply@gmail.com", "BGSS Company"));
        FileSystemResource res = new FileSystemResource(new File("com/server/blockchainserver/img/logo.png"));
        helper.addInline("logoImage", res, "image/png");
        helper.setText(htmlMsg, true);
        mailSender.send(mimeMessage);
    }


    /**
     * Sends an OTP verification email for an order.
     * This method constructs and sends an email to the user involved in an order,
     * containing an OTP (One-Time Password) for transaction verification. The email is
     * formatted using a custom HTML template which includes the OTP. Additionally, the email
     * features an inline image (logo) for branding purposes.
     *
     * @param order The {@link Order} object representing the order for which the OTP is being generated.
     * @param otp The OTP (One-Time Password) string to be sent to the user for transaction verification.
     *            This OTP is embedded within the HTML content of the email.
     * @throws MessagingException If there is a failure in creating or sending the email.
     * @throws IOException If there is an error in loading the HTML template or reading the logo image file.
     */
    public void sendVerificationOtpOrder(Order order, String otp) throws MessagingException, IOException {
        String recipientAddress = order.getEmail();
        String subject = "Order OTP Verification";

        String htmlTemplateWithdraw = loadEmailHtmlTemplate("sendOTPVerification.html");
        String htmlMsg = htmlTemplateWithdraw.replace("${OTP}", otp);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");

        helper.setTo(recipientAddress);
        helper.setSubject(subject);

        helper.setFrom(new InternetAddress("no-reply@gmail.com", "BGSS Company"));
        FileSystemResource res = new FileSystemResource(new File("path/to/your/logo.png"));
        helper.addInline("logoImage", res, "image/png");
        helper.setText(htmlMsg, true);
        mailSender.send(mimeMessage);
    }


    /**
     * Verifies the OTP for an order.
     * This method checks if the provided OTP matches the one stored in the database for the specified order.
     * If the OTP is valid and has not expired, the method updates the order's status to "NOT_RECEIVED" and deducts the order's total amount from the user's balance.
     * If the OTP is invalid or expired, the method throws an {@link IllegalArgumentException}.
     * @param otp The OTP (One-Time Password) string to be verified.
     * @param orderId The ID of the order for which the OTP is being verified.
     * @return {@code true} if the OTP is valid and the order's status is updated; otherwise, {@code false}.
     * @throws IllegalArgumentException if the OTP is invalid or expired.
     */
    public boolean verifyOtpForOrder(String otp, Long orderId) {
        Optional<OtpVerification> otpVerificationOpt = otpVerificationRepository
                .findByReferenceIdAndAssociatedEntityAndExpiresAtAfter(orderId.toString(), "Order", LocalDateTime.now());

        if (otpVerificationOpt.isPresent()) {
            OtpVerification otpVerification = otpVerificationOpt.get();
            if (otpVerification.getOtp().equals(otp) && !otpVerification.isExpired()) {
                otpVerificationRepository.delete(otpVerification);

                // Tìm và cập nhật trạng thái của đơn hàng
                Order orderToUpdate = orderRepository.findById(orderId)
                        .orElseThrow(() -> new IllegalArgumentException("Order with ID " + orderId + " not found."));

                List<OrderDetail> orderDetailList = orderDetailRepository.findAllByOrderId(orderToUpdate.getId());
                for (OrderDetail orderDetail: orderDetailList) {
                    Product product = productRepository.findById(orderDetail.getProduct().getId())
                            .orElseThrow(()-> new NotFoundException("Not found product with id: " + orderDetail.getProduct().getId()));

                    if(orderDetail.getProduct().getUnitOfStock() == 0){
                        throw new VerifyOTPOrderException("Payment for this order failed. One of the products you buy is out of stock.");
                    }
                    if(orderDetail.getQuantity() > product.getUnitOfStock()){
                        throw new VerifyOTPOrderException("Payment for this order failed. One of the products you buy is not enough quantity in store.");
                    }
                }

                if (orderToUpdate.getStatusReceived() == EReceivedStatus.UNVERIFIED) {
                    orderToUpdate.setStatusReceived(EReceivedStatus.NOT_RECEIVED);
                    deductBalance(orderToUpdate);
                    orderRepository.save(orderToUpdate);
                }
                return true;
            }
        } else {
            throw new IllegalArgumentException("Invalid or expired OTP");
        }
        return false;
    }


    /**
     * Sends an OTP verification email for a gold transaction.
     * This method constructs and sends an email to the user involved in a gold transaction,
     * containing an OTP (One-Time Password) for transaction verification. The email includes
     * a custom HTML template with the OTP embedded within it. Additionally, the email features
     * an inline image (logo) for branding purposes.
     *
     * @param transaction The {@link GoldTransaction} object representing the transaction for which
     *                    the OTP verification is being sent. This object contains information about
     *                    the transaction and the user involved.
     * @param otp The OTP (One-Time Password) string to be sent to the user for transaction verification.
     *            This OTP is embedded within the HTML content of the email.
     * @throws MessagingException If there is a failure in creating or sending the email.
     * @throws IOException If there is an error in loading the HTML template or reading the logo image file.
     */
    public void sendVerificationOtpTransaction(GoldTransaction transaction, String otp) throws MessagingException, IOException {
        String recipientAddress = transaction.getActionParty().getUser().getEmail();
        String subject = "Transaction OTP Verification";

        String htmlTemplate = loadEmailHtmlTemplate("sendOTPVerification.html");
        String htmlMsg = htmlTemplate.replace("${OTP}", otp);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");

        helper.setTo(recipientAddress);
        helper.setSubject(subject);

        helper.setFrom(new InternetAddress("no-reply@gmail.com", "BGSS Company"));
        FileSystemResource res = new FileSystemResource(new File("path/to/your/logo.png"));
        helper.addInline("logoImage", res, "image/png");
        helper.setText(htmlMsg, true);
        mailSender.send(mimeMessage);
    }

    /**
     * Sends an OTP verification email for a withdrawal request.
     * This method constructs and sends an email to the user who requested a withdrawal of gold,
     * containing an OTP (One-Time Password) for verifying the withdrawal request. The email is
     * formatted using a custom HTML template which includes the OTP. Additionally, the email
     * features an inline image (logo) for branding purposes.
     *
     * @param withdrawGold The {@link WithdrawGold} object representing the withdrawal request.
     *                     This object contains information about the request and the user involved.
     * @param otp The OTP (One-Time Password) string to be sent to the user for verifying the withdrawal request.
     *            This OTP is embedded within the HTML content of the email.
     * @throws MessagingException If there is a failure in creating or sending the email.
     * @throws IOException If there is an error in loading the HTML template or reading the logo image file.
     */
    public void sendVerificationOtpWithdraw(WithdrawGold withdrawGold, String otp) throws MessagingException, IOException {
        String recipientAddress = withdrawGold.getUserInfo().getUser().getEmail();
        String subject = "Withdrawal Gold Request OTP Verification";

        String htmlTemplate = loadEmailHtmlTemplateOtp();
        String htmlMsgOtp = htmlTemplate.replace("${OTP}", otp);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");

        helper.setTo(recipientAddress);
        helper.setSubject(subject);

        helper.setFrom(new InternetAddress("no-reply@gmail.com", "BGSS Company"));
        FileSystemResource res = new FileSystemResource(new File("path/to/your/logo.png"));
        helper.addInline("logoImage", res, "image/png");
        helper.setText(htmlMsgOtp, true);
        mailSender.send(mimeMessage);
    }


    /**
     * Verifies the OTP for a gold transaction.
     * This method checks if the provided OTP matches the one stored in the database for the specified gold transaction.
     * If the OTP is valid and has not expired, the method updates the transaction's status to "VERIFIED" and removes the OTP verification record.
     * If the OTP is invalid or expired, the method throws an {@link IllegalArgumentException}.
     * @param otp The OTP (One-Time Password) string to be verified.
     * @param transactionId The ID of the gold transaction for which the OTP is being verified.
     * @return {@code true} if the OTP is valid and the transaction's status is updated; otherwise, {@code false}.
     * @throws IllegalArgumentException if the OTP is invalid or expired.
     */
    @Transactional
    public boolean verifyOtpForTransaction(String otp, Long transactionId) {
        Optional<OtpVerification> otpVerificationOpt = otpVerificationRepository
                .findByReferenceIdAndAssociatedEntityAndExpiresAtAfter(transactionId.toString(), "GoldTransaction", LocalDateTime.now());

        if (otpVerificationOpt.isPresent()) {
            OtpVerification otpVerification = otpVerificationOpt.get();
            if (otpVerification.getOtp().equals(otp) && !otpVerification.isExpired()) {
                otpVerificationRepository.delete(otpVerification);

                GoldTransaction transactionToUpdate = goldTransactionRepository.findById(transactionId)
                        .orElseThrow(() -> new IllegalArgumentException("Transaction with ID " + transactionId + " not found."));
                if (transactionToUpdate.getTransactionVerification() == TransactionVerification.UNVERIFIED) {
                    transactionToUpdate.setTransactionVerification(TransactionVerification.VERIFIED);

                }
                return true;
            }
        } else {
            throw new IllegalArgumentException("Invalid or expired OTP");
        }
        return false;
    }

    /**
     * Verifies the OTP for a withdrawal request.
     * This method checks if the provided OTP matches the one stored in the database for the specified withdrawal request.
     * If the OTP is valid and has not expired, the method updates the withdrawal request's status to "VERIFIED" and removes the OTP verification record.
     * If the OTP is invalid or expired, the method throws an {@link IllegalArgumentException}.
     * @param otp The OTP (One-Time Password) string to be verified.
     * @param withdrawId The ID of the withdrawal request for which the OTP is being verified.
     * @return {@code true} if the OTP is valid and the withdrawal request's status is updated; otherwise, {@code false}.
     * @throws IllegalArgumentException if the OTP is invalid or expired.
     */
    public boolean verifyOtpForWithdrawal(Long userInfoId ,String otp, Long withdrawId) {
        Optional<OtpVerification> otpVerificationOpt = otpVerificationRepository
                .findByReferenceIdAndAssociatedEntityAndExpiresAtAfter(withdrawId.toString(), "WithdrawGold", LocalDateTime.now());
        if (otpVerificationOpt.isPresent()) {
            OtpVerification otpVerification = otpVerificationOpt.get();
            if (otpVerification.getOtp().equals(otp) && !otpVerification.isExpired()) {
                otpVerificationRepository.delete(otpVerification);

                WithdrawGold withdrawGold = withdrawGoldRepository.findById(withdrawId)
                        .orElseThrow(() -> new IllegalArgumentException("Transaction with ID " + withdrawId + " not found."));
                Optional<Product> product = productRepository.findById(withdrawGold.getProduct().getId());
                UserGoldInventory inventory = userInventoryRepository.findByUserInfoId(userInfoId);
                GoldUnit unit = withdrawGold.getGoldUnit();
                BigDecimal weightToWithdrawInOzAvailable = unit.convertToTroyOunce(BigDecimal.valueOf(withdrawGold.getAmount()));
                if (withdrawGold.getStatus() == WithdrawalStatus.REJECTED) {
                    throw new WithdrawalNotAllowedException("Your withdraw has been REJECTED!");
                }
                if (inventory.getTotalWeightOz().compareTo(weightToWithdrawInOzAvailable) < 0) {
                    withdrawGold.setStatus(WithdrawalStatus.REJECTED);
                    withdrawGoldRepository.save(withdrawGold);
                    throw new WithdrawalNotAllowedException("The gold in inventory is not enough to withdraw");
                }
                if (withdrawGold.getStatus() == WithdrawalStatus.UNVERIFIED) {
                    if (product.get().getUnitOfStock() == 0) {
                        withdrawGold.setStatus(WithdrawalStatus.REJECTED);
                        withdrawGoldRepository.save(withdrawGold);
                        throw new WithdrawalNotAllowedException("This product is currently out of stock");
                    }
                    product.get().setUnitOfStock(product.get().getUnitOfStock() - 1);

                    productRepository.save(product.get());

                    withdrawGold.setStatus(WithdrawalStatus.APPROVED);
                    withdrawGoldRepository.save(withdrawGold);
                }
                inventory.setTotalWeightOz(inventory.getTotalWeightOz().subtract(weightToWithdrawInOzAvailable));
                userInventoryRepository.save(inventory);
                return true;
            }
        } else {
            throw new IllegalArgumentException("Invalid or expired OTP");
        }
        return false;
    }

    /**
     * Generates an OTP for a gold transaction and sends an email with the OTP to the user involved in the transaction.
     *
     * @param transaction The {@link GoldTransaction} object representing the transaction for which the OTP is being generated.
     * @return The generated OTP string.
     * @throws MessagingException If there is a failure in creating or sending the email.
     * @throws IOException If there is an error in loading the HTML template or reading the logo image file.
     */
    public String generateOtpForTransaction(GoldTransaction transaction) throws MessagingException, IOException {

        if (transaction == null || transaction.getActionParty().getUser().getEmail() == null) {
            throw new IllegalArgumentException("Invalid or expired transaction");
        }
        otpVerificationRepository.findByReferenceIdAndAssociatedEntity(transaction.getId().toString(), "GoldTransaction")
                .ifPresent(otpVerification -> otpVerificationRepository.delete(otpVerification));
        String newOtp = String.format("%06d", ThreadLocalRandom.current().nextInt(100000, 999999));
        OtpVerification newOtpVerification = new OtpVerification(transaction.getId().toString(), "GoldTransaction");
        newOtpVerification.setOtp(newOtp);
        otpVerificationRepository.save(newOtpVerification);

        sendVerificationOtpTransaction(transaction, newOtp);
        return newOtp;
    }


    /**
     * Generates an OTP for a withdrawal request and sends an email with the OTP to the user involved in the withdrawal request.
     *
     * @param withdrawGold The {@link WithdrawGold} object representing the withdrawal request.
     * @return The generated OTP string.
     * @throws MessagingException If there is a failure in creating or sending the email.
     * @throws IOException If there is an error in loading the HTML template or reading the logo image file.
     */
    public String generateOtpForWithdraw(WithdrawGold withdrawGold) throws MessagingException, IOException {

        if (withdrawGold == null || withdrawGold.getUserInfo().getUser().getEmail() == null) {
            throw new IllegalArgumentException("Invalid or expired transaction");
        }
        otpVerificationRepository.findByReferenceIdAndAssociatedEntity(withdrawGold.getId().toString(), "WithdrawGold")
                .ifPresent(otpVerification -> otpVerificationRepository.delete(otpVerification));
        String newOtp = String.format("%06d", ThreadLocalRandom.current().nextInt(100000, 999999));
        OtpVerification newOtpVerification = new OtpVerification(withdrawGold.getId().toString(), "WithdrawGold");
        newOtpVerification.setOtp(newOtp); // Set the OTP
        otpVerificationRepository.save(newOtpVerification);
        sendVerificationOtpWithdraw(withdrawGold, newOtp);
        return newOtp;
    }


    /**
     * Generates an OTP for an order and sends an email with the OTP to the user involved in the order.
     *
     * @param order The {@link Order} object representing the order for which the OTP is being generated.
     * @return The generated OTP string.
     * @throws MessagingException If there is a failure in creating or sending the email.
     * @throws IOException If there is an error in loading the HTML template or reading the logo image file.
     */
    public String generateOtpForOrder(Order order) throws MessagingException, IOException {
        if (order == null || order.getEmail() == null) {
            throw new IllegalArgumentException("Order or Order Email cannot be null");
        }
        otpVerificationRepository.findByReferenceIdAndAssociatedEntity(order.getId().toString(), "Order")
                .ifPresent(otpVerification -> otpVerificationRepository.delete(otpVerification));

        String newOtp = String.format("%06d", ThreadLocalRandom.current().nextInt(100000, 999999));
        OtpVerification newOtpVerification = new OtpVerification(order.getId().toString(), "Order");
        newOtpVerification.setOtp(newOtp); // Set the OTP
        otpVerificationRepository.save(newOtpVerification);

        // Cập nhật trạng thái đơn hàng trước khi gửi OTP qua email là một lựa chọn tốt
        order.setStatusReceived(EReceivedStatus.UNVERIFIED);
        orderRepository.save(order);

        sendVerificationOtpOrder(order, newOtp);
        return newOtp;
    }


    /**
     * Deduces the specified amount from the user's balance.
     *
     * @param order The {@link Order} object representing the order for which the balance is being deducted.
     * @throws OrderException If there is an insufficient balance to confirm the order.
     */
    private void deductBalance(Order order) {

        Balance balance = balanceRepository.findBalanceByUserInfoId(order.getUser().getId())
                .orElseThrow(() -> new NotFoundException("Balance not found"));

        if (balance.getBalance().compareTo(order.getTotalAmount()) >= 0) {
            balance.setBalance(balance.getBalance().subtract(order.getTotalAmount()));
            balanceRepository.save(balance);
        } else {
            throw new OrderException("Insufficient balance to confirm this order");
        }
    }


    /**
     * Generates a verification token for the specified user.
     *
     * @param user The {@link User} object representing the user for whom the verification token is being generated.
     * @return The generated verification token string.
     */
    public String generateVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, user);
        verificationTokenRepository.save(verificationToken);
        return token;
    }

    @Transactional
    public void sendContractDetails(Contract contract) throws IOException, MessagingException {
        String recipientAddress = contract.getTransaction().getActionParty().getUser().getEmail();
        String htmlTemplate = loadEmailHtmlTemplate("contract.html");

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");
        helper.setTo(recipientAddress);
        helper.setSubject("Contract Details");


        // Thay thế các placeholder trong template bằng dữ liệu thực
        htmlTemplate = htmlTemplate.replace("[id]", String.valueOf(contract.getId()));
        htmlTemplate = htmlTemplate.replace("[actionPartyFullName]", contract.getActionPartyFullName());
        htmlTemplate = htmlTemplate.replace("[actionPartyAddress]", contract.getActionPartyAddress());
        htmlTemplate = htmlTemplate.replace("[quantity]", contract.getQuantity().toString());
        htmlTemplate = htmlTemplate.replace("[pricePerOunce]", contract.getPricePerOunce().toString());
        htmlTemplate = htmlTemplate.replace("[totalCostOrProfit]", contract.getTotalCostOrProfit().toString());
        htmlTemplate = htmlTemplate.replace("[transactionType]", contract.getTransactionType().name());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedCreatedAt = formatter.format(contract.getCreatedAt());
        htmlTemplate = htmlTemplate.replace("[createdAt]", formattedCreatedAt);
        htmlTemplate = htmlTemplate.replace("[confirmingParty]", contract.getConfirmingParty());
        htmlTemplate = htmlTemplate.replace("[goldUnit]", contract.getGoldUnit().name());
        htmlTemplate = htmlTemplate.replace("[contractStatus]", contract.getContractStatus().name());
        htmlTemplate = htmlTemplate.replace("[signatureActionParty]", contract.getSignatureActionParty());
        htmlTemplate = htmlTemplate.replace("[signatureConfirmingParty]", contract.getSignatureConfirmingParty());

        helper.setFrom(new InternetAddress("no-reply@gmail.com", "BGSS Company"));

        helper.setText(htmlTemplate, true);
        mailSender.send(mimeMessage);
    }


}
