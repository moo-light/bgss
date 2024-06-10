package com.server.blockchainserver.platform.platform_services.services_interface;


import com.server.blockchainserver.exeptions.NotFoundException;
import com.server.blockchainserver.platform.data_transfer_object.ContractDTO;
import com.server.blockchainserver.platform.data_transfer_object.TransactionDTO;
import com.server.blockchainserver.platform.data_transfer_object.WithdrawGoldDTO;
import com.server.blockchainserver.platform.data_transfer_object.WithdrawGoldDefault;
import com.server.blockchainserver.platform.entity.WithdrawGold;
import com.server.blockchainserver.platform.entity.enums.GoldUnit;
import com.server.blockchainserver.platform.entity.enums.TransactionType;
import com.server.blockchainserver.platform.entity.enums.WithdrawRequirement;
import jakarta.mail.MessagingException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

public interface TransactionServices {
    TransactionDTO processTransaction(Long userInfoId, BigDecimal quantityInOz,
                                      BigDecimal pricePerOz, TransactionType type, GoldUnit goldUnit) throws
            NotFoundException, IllegalStateException, IOException, MessagingException;

    WithdrawGoldDefault requestWithdrawal(Long userInfoId, BigDecimal weightToWithdraw, GoldUnit unit, WithdrawRequirement withdrawRequirement, Long productId) throws MessagingException, IOException;

    WithdrawGoldDefault handleWithdrawal(Long withdrawalId, String actionStr, String withdrawQrCode) throws Exception;

    WithdrawGoldDTO cancelWithdrawal(Long withdrawalId, String cancellationReason, String username);

    List<TransactionDTO> transactionList();

    List<TransactionDTO> transactionListUser(Long userInfoId);

    List<WithdrawGoldDTO> withdraws();

    List<WithdrawGoldDTO> withdrawsUserInfo(long userInfoId);

    TransactionDTO transactionAcceptedForMobile(Long transactionId, MultipartFile signature, String publicKey) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, InvalidKeyException;

    TransactionDTO transactionAcceptedForWeb(Long transactionId, String publicKey) throws IOException;

    ContractDTO verifyContract(Long contractId, String privateKey, String publicKey) throws Exception;

    TransactionDTO updateTransactionSignatureForMobile(Long transactionId, MultipartFile signature, String publicKey) throws IOException;

    boolean userConfirmReceived(Long withdrawId) throws Exception;
}
