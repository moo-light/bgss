package com.server.blockchainserver.platform.platform_services.transactionservice;

import com.server.blockchainserver.exeptions.NotFoundException;
import com.server.blockchainserver.platform.data_transfer_object.ContractDTO;
import com.server.blockchainserver.platform.data_transfer_object.TransactionDTO;
import com.server.blockchainserver.platform.data_transfer_object.WithdrawGoldDTO;
import com.server.blockchainserver.platform.data_transfer_object.WithdrawGoldDefault;
import com.server.blockchainserver.platform.entity.GoldTransaction;
import com.server.blockchainserver.platform.entity.enums.GoldUnit;
import com.server.blockchainserver.platform.entity.enums.TransactionType;
import com.server.blockchainserver.platform.entity.enums.WithdrawRequirement;
import com.server.blockchainserver.platform.platform_services.services_interface.TransactionServices;
import com.server.blockchainserver.platform.server.TransactionAction;
import com.server.blockchainserver.platform.server.TransactionManagement;
import jakarta.mail.MessagingException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionServices {

    @Autowired
    private TransactionAction transaction;

    @Autowired
    private TransactionManagement transactionManagement;

    @Override
    public TransactionDTO processTransaction(Long userInfoId, BigDecimal quantityInOz,
                                             BigDecimal pricePerOz, TransactionType type, GoldUnit goldUnit)
            throws NotFoundException, IllegalStateException, IOException, MessagingException {
        return transaction.processTransaction(userInfoId, quantityInOz, pricePerOz, type, goldUnit);
    }

    @Override
    public WithdrawGoldDefault requestWithdrawal(Long userInfoId, BigDecimal weightToWithdraw, GoldUnit unit, WithdrawRequirement withdrawRequirement, Long productId) throws MessagingException, IOException {
        return transaction.requestWithdrawal(userInfoId, weightToWithdraw, unit, withdrawRequirement, productId);
    }

    @Override
    public WithdrawGoldDefault handleWithdrawal(Long withdrawalId, String actionStr, String withdrawQrCode) throws Exception {
        return transaction.handleWithdrawal(withdrawalId, actionStr, withdrawQrCode);
    }

    @Override
    public WithdrawGoldDTO cancelWithdrawal(Long withdrawalId, String cancellationReason, String username) {
        return transaction.cancelWithdrawal(withdrawalId, cancellationReason, username);
    }

    @Override
    public List<TransactionDTO> transactionList() {
        return transactionManagement.transactionList();
    }

    @Override
    public List<TransactionDTO> transactionListUser(Long userInfoId) {
        return transactionManagement.transactionListUser(userInfoId);
    }



    @Override
    public List<WithdrawGoldDTO> withdraws() {
        return transactionManagement.withdrawGoldDTOList();
    }

    @Override
    public List<WithdrawGoldDTO> withdrawsUserInfo(long userInfoId) {
        return transactionManagement.withdrawGoldDTOListUserInfo(userInfoId);
    }

    @Override
    public TransactionDTO transactionAcceptedForMobile(Long transactionId, MultipartFile signature, String publicKey) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, InvalidKeyException {
        return transaction.transactionAcceptedForMobile(transactionId, signature, publicKey);
    }

    @Override
    public TransactionDTO transactionAcceptedForWeb(Long transactionId, String publicKey) throws IOException {
        return transaction.transactionAcceptedWeb(transactionId, publicKey);
    }


    @Override
    public ContractDTO verifyContract(Long contractId, String privateKey, String publicKey) throws Exception {
        return transaction.verifyContract(contractId, privateKey, publicKey);
    }

    @Override
    public TransactionDTO updateTransactionSignatureForMobile(Long transactionId, MultipartFile signature, String publicKey) throws IOException {
        return transaction.updateTransactionSignatureForMobile(transactionId, signature, publicKey);
    }

    @Override
    public boolean userConfirmReceived(Long withdrawId) throws Exception {
        return transaction.userConfirmReceived(withdrawId);
    }
    // kiểu minh gắn tầng services làm gì rồi ko gắn cái mapper ở đây luôn đi để 1 dòng code chi cho dư v
//    public WithdrawGoldDTO getWithdrawQrCode(String qrCode) {
//        return transactionManagement.getWithdrawGoldByQrCode(qrCode);
//    }


}
