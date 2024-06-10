package com.server.blockchainserver.platform.server;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.blockchainserver.dto.gold_inventory_dto.CancellationMessageDTO;
import com.server.blockchainserver.dto.product_dto.ProductDTOs;
import com.server.blockchainserver.dto.product_dto.ProductImagesDTO;
import com.server.blockchainserver.exeptions.NotFoundException;

import com.server.blockchainserver.exeptions.OrderException;
import com.server.blockchainserver.exeptions.UserConfirmWithdrawException;
import com.server.blockchainserver.exeptions.WithdrawalNotAllowedException;
import com.server.blockchainserver.models.enums.EGoldOptionType;
import com.server.blockchainserver.models.shopping_model.*;
import com.server.blockchainserver.models.user_model.Balance;
import com.server.blockchainserver.models.user_model.User;
import com.server.blockchainserver.models.user_model.UserInfo;
import com.server.blockchainserver.platform.data_transfer_object.ContractDTO;
import com.server.blockchainserver.platform.data_transfer_object.TransactionDTO;
import com.server.blockchainserver.platform.data_transfer_object.WithdrawGoldDTO;
import com.server.blockchainserver.platform.data_transfer_object.WithdrawGoldDefault;
import com.server.blockchainserver.platform.entity.*;
import com.server.blockchainserver.platform.entity.enums.*;
//import com.server.blockchainserver.platform.hyperledgerfabric.network.FabricGateway;
import com.server.blockchainserver.platform.repositories.*;
import com.server.blockchainserver.repository.shopping_repository.ProductRepository;
import com.server.blockchainserver.repository.signature_repository.OrganizationSignatureRepository;
import com.server.blockchainserver.repository.user_repository.SecretKeyRepository;
import com.server.blockchainserver.repository.user_repository.UserInfoRepository;
import com.server.blockchainserver.repository.user_repository.UserRepository;
import com.server.blockchainserver.server.BlockchainServer;
import com.server.blockchainserver.server.MailAuthentication;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class TransactionAction {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private GoldInventoryRepository goldInventoryRepository;
    @Autowired
    private UserGoldInventoryRepository userInventoryRepository;
    @Autowired
    private GoldTransactionRepository goldTransactionRepository;
    @Autowired
    private WithdrawGoldRepository withdrawGoldRepository;
    @Autowired
    private CancellationMessageRepository cancellationMessageRepository;
    @Autowired
    private BlockchainServer blockchainServer;
    @Autowired
    private OrganizationSignatureRepository organizationSignatureRepository;
    @Autowired
    private ContractRepository contractRepository;
    @Autowired
    private MailAuthentication mailAuthentication;
    @Autowired
    private SecretKeyRepository secretKeyRepository;
    @Autowired
    private ContractHistoryRepository contractHistoryRepository;
//   @Autowired
//   private FabricGateway fabricGateway;
    @Autowired
    private ProductRepository productRepository;


    /**
     * Processes a transaction for buying or selling gold.
     *
     * @param userId       the ID of the user initiating the transaction
     * @param quantityInOz the quantity of gold to be bought or sold, in ounces
     * @param pricePerOz   the price per ounce of gold
     * @param type         the type of transaction, either BUY or SELL
     * @param goldUnit     the unit of measurement for the gold, either TROY_OUNCE or GRAM
     * @return a TransactionDTO containing the details of the accepted transaction
     * @throws NotFoundException     if the user info or gold inventory is not found
     * @throws IllegalStateException if there is not enough gold or money to complete the transaction
     * @throws IOException           if there is an error reading the transaction signature file
     * @throws MessagingException    if there is an error sending the transaction confirmation email
     */
    @Transactional
    public TransactionDTO processTransaction(Long userId, BigDecimal quantityInOz, BigDecimal pricePerOz,
                                             TransactionType type, GoldUnit goldUnit) throws NotFoundException, IllegalStateException, IOException, MessagingException {

        BigDecimal convertQuantityInOz = goldUnit.convertToTroyOunce(quantityInOz);

        UserInfo userInfo = userInfoRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User info ID: " + userId + " not found"));

        UserGoldInventory userInventory = userInfo.getInventory();

        GoldInventory goldInventory = goldInventoryRepository.findAll().get(0);

        if (goldInventory.getTotalWeightKg().compareTo(convertQuantityInOz) < 0) {
            throw new IllegalStateException("Insufficient gold in GoldInventory.");
        }

        BigDecimal totalAmount = convertQuantityInOz.multiply(pricePerOz);

        switch (type) {
            case BUY:
                if (userInfo.getBalance().getBalance().compareTo(totalAmount) < 0) {
                    throw new IllegalStateException("Not enough money to buy");
                }
                break;
            case SELL:
                // Check if the user has enough gold to sell
                if (userInventory.getTotalWeightOz().compareTo(convertQuantityInOz) < 0) {
                    throw new IllegalStateException("Not enough gold to sell");
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid transaction type");
        }


        return recordTransaction(userInfo, quantityInOz, pricePerOz, totalAmount, type, goldUnit);
    }

    @Transactional
    public TransactionDTO transactionAcceptedForMobile(Long transactionId, MultipartFile signature, String publicKey) throws IOException {
        validateTransactionInputs(transactionId, publicKey);

        GoldTransaction transaction = fetchAndValidateTransaction(transactionId, signature, publicKey);

        BigDecimal convertQuantityInOz = transaction.getGoldUnit().convertToTroyOunce(transaction.getQuantity());
        BigDecimal totalAmount = convertQuantityInOz.multiply(transaction.getPricePerOunce());
        processTransactionBasedOnType(transaction, convertQuantityInOz, totalAmount);

        if (signature == null || signature.isEmpty()) {
            throw new IOException("Invalid signature");
        } else {
            transaction.setTransactionSignature(TransactionSignature.SIGNED);
        }
        transaction.setTransactionStatus(TransactionStatus.CONFIRMED);
        transaction.setTransactionVerification(TransactionVerification.VERIFIED);
        goldTransactionRepository.save(transaction);

        return getTransactionDTO(transaction, totalAmount, signature);
    }

    @Transactional
    public TransactionDTO transactionAcceptedWeb(Long transactionId, String publicKey) throws IOException {
        validateTransactionInputs(transactionId, publicKey);

        GoldTransaction transaction = fetchAndValidateTransaction(transactionId, null, publicKey);

        BigDecimal convertQuantityInOz = transaction.getGoldUnit().convertToTroyOunce(transaction.getQuantity());
        BigDecimal totalAmount = convertQuantityInOz.multiply(transaction.getPricePerOunce());
        processTransactionBasedOnType(transaction, convertQuantityInOz, totalAmount);

        // Đánh dấu giao dịch là không có chữ ký cho phiên bản web
        transaction.setTransactionSignature(TransactionSignature.UNSIGNED);
        transaction.setTransactionStatus(TransactionStatus.CONFIRMED);
        transaction.setTransactionVerification(TransactionVerification.VERIFIED);

        //ContractDTO contract = recordContract(transaction.getActionParty(), transaction.getQuantity(), transaction.getPricePerOunce(), totalAmount, transaction.getTransactionType(), transaction.getGoldUnit(), null, transaction);
        return new TransactionDTO(
                transaction.getId(),
                transaction.getQuantity(),
                transaction.getPricePerOunce(),
                transaction.getTotalCostOrProfit(),
                transaction.getTransactionType(),
                transaction.getCreatedAt(),
                transaction.getActionParty().getId(),
                transaction.getGoldUnit(),
                transaction.getConfirmingParty(),
                transaction.getTransactionVerification(),
                transaction.getTransactionSignature(),
                transaction.getTransactionStatus(),
                null
        );
    }

    @Transactional
    public TransactionDTO updateTransactionSignatureForMobile(Long transactionId, MultipartFile signature, String publicKey) throws IOException, NotFoundException, IllegalArgumentException {
        validateTransactionInputs(transactionId, publicKey);

        GoldTransaction transaction = goldTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new NotFoundException("Gold transaction ID: " + transactionId + " not found"));

        BigDecimal convertQuantityInOz = transaction.getGoldUnit().convertToTroyOunce(transaction.getQuantity());
        BigDecimal totalAmount = convertQuantityInOz.multiply(transaction.getPricePerOunce());

        UserInfo userInfo = userInfoRepository.findById(transaction.getActionParty().getId())
                .orElseThrow(() -> new NotFoundException("User info ID: " + transaction.getActionParty().getId() + " not found"));
        SecretKey secretKeyValue = secretKeyRepository.findByUserInfo(userInfo)
                .orElseThrow(() -> new NotFoundException("Secret key for user ID: " + userInfo.getId() + " not found"));
        String secretKeyStr = secretKeyValue.getPublicKey();
        if (!secretKeyStr.equals(publicKey)) {
            throw new IllegalArgumentException("Secret key does not match");
        }
        if (signature == null || signature.isEmpty()) {
            throw new IOException("Signature not empty");
        }
        transaction.setTransactionSignature(TransactionSignature.SIGNED);
        if (transaction.getTransactionVerification() == TransactionVerification.UNVERIFIED) {
            transaction.setTransactionVerification(TransactionVerification.VERIFIED);
        }
        goldTransactionRepository.save(transaction);

        return getTransactionDTO(transaction, totalAmount, signature);
    }

    @Transactional
    public TransactionDTO getTransactionDTO(GoldTransaction transaction, BigDecimal totalAmount, MultipartFile signature) throws IOException {
        ContractDTO contract = switch (transaction.getTransactionType()) {
            case BUY ->
                    recordContract(transaction.getActionParty(), transaction.getQuantity(), transaction.getPricePerOunce(), totalAmount.negate(), transaction.getTransactionType(), transaction.getGoldUnit(), signature, transaction);
            case SELL ->
                    recordContract(transaction.getActionParty(), transaction.getQuantity(), transaction.getPricePerOunce(), totalAmount, transaction.getTransactionType(), transaction.getGoldUnit(), signature, transaction);
        };
        return new TransactionDTO(
                transaction.getId(),
                transaction.getQuantity(),
                transaction.getPricePerOunce(),
                transaction.getTotalCostOrProfit(),
                transaction.getTransactionType(),
                transaction.getCreatedAt(),
                transaction.getActionParty().getId(),
                transaction.getGoldUnit(),
                transaction.getConfirmingParty(),
                transaction.getTransactionVerification(),
                transaction.getTransactionSignature(),
                transaction.getTransactionStatus(),
                contract
        );
    }


    @Transactional
    public void processTransactionBasedOnType(GoldTransaction transaction, BigDecimal convertQuantityInOz, BigDecimal totalAmount)
            throws IllegalStateException, IOException {
        UserInfo userInfo = transaction.getActionParty();
        UserGoldInventory userInventory = userInfo.getInventory();
        GoldInventory goldInventory = goldInventoryRepository.findAll().get(0); // Consider optimizing this database call

        switch (transaction.getTransactionType()) {
            case BUY:
                if (transaction.getActionParty().getBalance().getBalance().compareTo(totalAmount) < 0) {
                    transaction.setTransactionStatus(TransactionStatus.REJECTED);
                    throw new IllegalStateException("Not enough money to buy");
                }
                // Subtract cost from user balance and add gold to user's inventory
                updateUserBalance(userInfo, totalAmount.negate());
                updateInventories(goldInventory, userInventory, convertQuantityInOz, true);
                break;
            case SELL:
                if (userInventory.getTotalWeightOz().compareTo(transaction.getQuantity()) < 0) {
                    transaction.setTransactionStatus(TransactionStatus.REJECTED);
                    throw new IllegalStateException("Not enough gold to sell");
                }
                // Add profit to user balance and remove gold from user's inventory
                updateUserBalance(userInfo, totalAmount);
                updateInventories(goldInventory, userInventory, convertQuantityInOz, false);
                break;
            default:
                throw new IllegalArgumentException("Invalid transaction type");
        }
    }


    private void validateTransactionInputs(Long transactionId, String publicKey) throws NotFoundException {
        if (transactionId == null || publicKey == null || publicKey.isEmpty()) {
            throw new NotFoundException("Transaction ID, signature, or secret key is null or empty");
        }
    }

    private GoldTransaction fetchAndValidateTransaction(Long transactionId, MultipartFile signature, String publicKey)
            throws NotFoundException, IllegalArgumentException {

        GoldTransaction transaction = goldTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new NotFoundException("Gold transaction ID: " + transactionId + " not found"));
        if (signature == null || signature.isEmpty()) {
            transaction.setTransactionSignature(TransactionSignature.UNSIGNED);
        }
        UserInfo userInfo = userInfoRepository.findById(transaction.getActionParty().getId())
                .orElseThrow(() -> new NotFoundException("User info ID: " + transaction.getActionParty().getId() + " not found"));
        SecretKey secretKeyValue = secretKeyRepository.findByUserInfo(userInfo)
                .orElseThrow(() -> new NotFoundException("Secret key for user ID: " + userInfo.getId() + " not found"));
        String secretKeyStr = secretKeyValue.getPublicKey();
        if (!secretKeyStr.equals(publicKey)) {
            transaction.setTransactionStatus(TransactionStatus.REJECTED);
            throw new IllegalArgumentException("Secret key does not match");
        }
        return transaction;
    }


    /**
     * Verifies the digital signature of a contract using the provided public and private keys.
     * This method first retrieves the contract by its ID and checks its current status to ensure it is not already signed.
     * It then converts the provided public and private key strings into their respective key objects.
     * A digital signature is created for the contract using the private key, and this signature is verified using the public key.
     * If the verification is successful, the contract's status is updated to {@link ContractStatus#DIGITAL_SIGNED}.
     *
     * @param contractId The unique identifier of the contract to be verified.
     * @param publicKey  The public key as a String, used to verify the digital signature.
     * @param privateKey The private key as a String, used to create the digital signature.
     * @return A {@link ContractDTO} object representing the contract after verification.
     * @throws Exception If there is an error during the process, including if the contract is not found,
     *                   if the contract status check fails, if the keys cannot be converted, if the signature cannot be created,
     *                   or if the signature verification fails.
     */
    @Transactional
    public ContractDTO verifyContract(Long contractId, String publicKey, String privateKey) throws Exception {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new NotFoundException("Not found contract"));
        checkContractStatus(contract);
        PublicKey publicKeyStr = convertStringToPublicKey(publicKey);
        PrivateKey privateKeyStr = convertStringToPrivateKey(privateKey);
        String digitalSignature = createContractSignature(contract, privateKeyStr);
        ContractDTO contractDTO = convertContractToDTO(contract);
        if (isSignatureInvalid(contractDTO, publicKeyStr, digitalSignature)) {
            throw new SignatureException("Verification of contract signature failed! Please re-verify the signature");
        }
        updateContractStatusAndDigitalSignature(contract, ContractStatus.DIGITAL_SIGNED, digitalSignature);
        contract.getTransaction().setTransactionStatus(TransactionStatus.COMPLETED);

//       fabricGateway.createAsset(contractDTO.getId(), contractDTO.getActionParty(),
//               contractDTO.getFullName(), contractDTO.getAddress(), contractDTO.getQuantity(),
//               contractDTO.getPricePerOunce(), contractDTO.getTotalCostOrProfit(),
//               contractDTO.getTransactionType(), contractDTO.getCreatedAt(),
//               contractDTO.getConfirmingParty(), contractDTO.getGoldUnit(),
//               contractDTO.getSignatureActionParty(), contractDTO.getSignatureConfirmingParty(),
//               contractDTO.getContractStatus());
        mailAuthentication.sendContractDetails(contract);
        return contractDTO;
    }


    /**
     * Verifies the integrity of a contract by checking the digital signature against the provided public key and contract data.
     * This method first retrieves the contract by its ID and converts the provided public key from a string to a PublicKey object.
     * It then prepares the contract data for verification by converting the contract details into a byte array.
     * The digital signature, provided as a Base64 encoded string, is decoded into a byte array.
     * Finally, it verifies the digital signature using the public key and the prepared contract data.
     *
     * @param contractId   The unique identifier of the contract to be verified.
     * @param publicKeyStr The public key as a String, used to verify the digital signature.
     * @param signatureStr The digital signature as a Base64 encoded String.
     * @return true if the signature is valid and matches the contract data using the provided public key, false otherwise.
     * @throws Exception If there is an error during the process, including if the contract is not found,
     *                   if the public key conversion fails, or if the signature verification process fails.
     */
    @Transactional
    public boolean verifyContractIntegrity(Long contractId, String publicKeyStr, String signatureStr) throws Exception {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new NotFoundException("Not found contract"));
        ContractDTO contractDTO = convertContractToDTO(contract);

        // Chuyển đổi publicKeyStr từ String sang PublicKey
        PublicKey publicKey = convertStringToPublicKey(publicKeyStr);

        // Chuẩn bị dữ liệu từ contractDTO để xác minh
        byte[] dataBytes = prepareContractDataBytes(contractDTO);

        // Giải mã signatureStr từ Base64 để lấy byte[] của chữ ký
        byte[] signatureBytes = Base64.getDecoder().decode(signatureStr);

        // Xác minh chữ ký
        return isSignatureValid(signatureBytes, publicKey, dataBytes);
    }


    @Transactional
    public Contract updateContract(Long userInfoId, Long contractId, Contract updatedContract, String publicKey, String privateKey) throws Exception {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Contract not found"));
        PublicKey publicKeyStr = convertStringToPublicKey(publicKey);
        PrivateKey privateKeyStr = convertStringToPrivateKey(privateKey);
        String digitalSignature = createContractSignature(contract, privateKeyStr);

        updateContractInformation(contract, updatedContract);

        ContractDTO contractDTO = convertContractToDTO(contract);
        if (isSignatureInvalid(contractDTO, publicKeyStr, digitalSignature)) {
            throw new SignatureException("Verification of contract signature failed");
        }

        ContractHistory history = getContractHistory(userInfoId, contract, digitalSignature);

        contractHistoryRepository.save(history);

        return contractRepository.save(contract);
    }

    private void updateContractInformation(Contract contract, Contract updatedContract) {
        contract.setActionParty(updatedContract.getActionParty());
        contract.setActionPartyFullName(updatedContract.getActionPartyFullName());
        contract.setActionPartyAddress(updatedContract.getActionPartyAddress());
        contract.setQuantity(updatedContract.getQuantity());
        contract.setPricePerOunce(updatedContract.getPricePerOunce());
        contract.setTotalCostOrProfit(updatedContract.getTotalCostOrProfit());
        contract.setTransactionType(updatedContract.getTransactionType());
        contract.setCreatedAt(updatedContract.getCreatedAt());
        contract.setConfirmingParty(updatedContract.getConfirmingParty());
        contract.setGoldUnit(updatedContract.getGoldUnit());
        contract.setSignatureActionParty(updatedContract.getSignatureActionParty());
        contract.setSignatureConfirmingParty(updatedContract.getSignatureConfirmingParty());
        contract.setTransaction(updatedContract.getTransaction());
        contract.setContractStatus(updatedContract.getContractStatus());
        contract.setContractDigitalSignature(updatedContract.getContractDigitalSignature());
    }


    private ContractHistory getContractHistory(Long userInfoId, Contract contract, String digitalSignature) {
        UserInfo userInfo = userInfoRepository.findById(userInfoId)
                .orElseThrow(() -> new NotFoundException("User info not found: " + userInfoId));
        List<ContractHistory> historyList = contractHistoryRepository.findByContract(contract);
        int historyCount = historyList.size();
        ContractHistory history = new ContractHistory();
        history.setContract(contract);
        history.setContractVersion("v" + (historyCount + 1) + ".0"); // Ví dụ, bạn cần một cơ chế để quản lý phiên bản
        history.setContractChangeBy(userInfo.getUser().getEmail()); // Cập nhật thông tin này dựa trên người dùng thực hiện
        history.setContractChangeDescription("Contract updated");
        history.setContractChangeDate(new Date());
        history.setContractDigitalSignature(digitalSignature); // Giả sử bạn có cơ chế sinh chữ ký số
        return history;
    }


    /**
     * Converts a Contract entity into a ContractDTO object.
     * This method maps the fields of a Contract entity to a new ContractDTO object to facilitate data transfer
     * and abstraction from the entity model. It is particularly useful for sending contract data to clients
     * without exposing the entity model directly.
     *
     * @param contract The Contract entity to be converted.
     * @return A ContractDTO object containing the data from the provided Contract entity.
     */
    @Transactional
    public ContractDTO convertContractToDTO(Contract contract) {
        ContractDTO contractDTO = new ContractDTO();
        contractDTO.setId(contract.getId());
        contractDTO.setActionParty(contract.getActionParty().getId());
        contractDTO.setFullName(contract.getActionPartyFullName());
        contractDTO.setAddress(contract.getActionPartyAddress());
        contractDTO.setQuantity(contract.getQuantity());
        contractDTO.setPricePerOunce(contract.getPricePerOunce());
        contractDTO.setTotalCostOrProfit(contract.getTotalCostOrProfit());
        contractDTO.setTransactionType(contract.getTransactionType());
        contractDTO.setCreatedAt(contract.getCreatedAt());
        contractDTO.setConfirmingParty(contract.getConfirmingParty());
        contractDTO.setGoldUnit(contract.getGoldUnit());
        contractDTO.setSignatureActionParty(contract.getSignatureActionParty());
        contractDTO.setSignatureConfirmingParty(contract.getSignatureConfirmingParty());
        contractDTO.setContractStatus(contract.getContractStatus());
        return contractDTO;
    }

    /**
     * Checks the status of a contract and its associated transaction to ensure that it is in a valid state for further processing.
     * This method verifies that the contract has not already been digitally signed, and that the transaction associated with the contract
     * has been verified and signed. If any of these conditions are not met, a {@link WithdrawalNotAllowedException} is thrown.
     *
     * @param contract The contract entity to be checked. It must not be null and should be a valid contract entity with an associated transaction.
     * @throws WithdrawalNotAllowedException if the contract has already been digitally signed, or if the associated transaction is either
     *                                       not verified or not signed. This exception indicates that the contract or transaction is not in a valid state for further processing.
     */
    @Transactional
    public void checkContractStatus(Contract contract) {
        if (contract.getContractStatus() == ContractStatus.DIGITAL_SIGNED) {
            throw new WithdrawalNotAllowedException("Contract signature is already signed");
        }
        if (contract.getTransaction().getTransactionVerification() == TransactionVerification.UNVERIFIED) {
            throw new WithdrawalNotAllowedException("Transaction signature is not verified");
        }
        if (contract.getTransaction().getTransactionSignature() == TransactionSignature.UNSIGNED) {
            throw new WithdrawalNotAllowedException("Transaction signature is unsigned");
        }
    }

    /**
     * Updates the status of a given contract in the database.
     * This method sets the contract's status to the specified value and saves the updated contract
     * to the repository, effectively persisting the changes to the database.
     *
     * @param contract The contract entity whose status is to be updated. This object must not be null.
     * @param status   The new status to be assigned to the contract. This value must not be null.
     */
    @Transactional
    public void updateContractStatusAndDigitalSignature(Contract contract, ContractStatus status, String digitalSignature) {
        contract.setContractStatus(status);
        contract.setContractDigitalSignature(digitalSignature);
        contractRepository.save(contract);
    }

    /**
     * Verifies the digital signature of a contract against the provided public key and the contract data.
     * This method decodes the digital signature from a Base64 encoded string, prepares the contract data for verification,
     * and then uses the public key to verify the signature against the contract data.
     *
     * @param contractDTO      The Contract Data Transfer Object (DTO) containing the contract data to be verified.
     * @param publicKey        The public key used to verify the digital signature.
     * @param digitalSignature The digital signature of the contract data, encoded as a Base64 string.
     * @return true if the signature is valid and matches the contract data using the provided public key, false otherwise.
     * @throws Exception If an error occurs during the signature verification process. This could be due to issues with decoding the signature,
     *                   preparing the contract data, or the verification process itself.
     */
    @Transactional
    public boolean isSignatureInvalid(ContractDTO contractDTO, PublicKey publicKey, String digitalSignature) throws Exception {
        byte[] data = prepareContractDataBytes(contractDTO);
        byte[] signatureBytes = Base64.getDecoder().decode(digitalSignature);
        return !isSignatureValid(signatureBytes, publicKey, data);
    }

    /**
     * Prepares the contract data for digital signature verification by converting the ContractDTO object into a byte array.
     * This method serializes the ContractDTO object into a JSON string using Jackson's ObjectMapper, then converts this string
     * into a byte array using UTF-8 encoding. The resulting byte array is used for digital signature operations, such as signing
     * or verifying a signature.
     *
     * @param contractDTO The ContractDTO object containing the contract data to be prepared.
     * @return A byte array representing the serialized JSON string of the ContractDTO object.
     * @throws JsonProcessingException If an error occurs during the serialization of the ContractDTO object.
     */
    @Transactional
    public byte[] prepareContractDataBytes(ContractDTO contractDTO) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(contractDTO);
        return json.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Converts a string representation of a public key into a {@link PublicKey} object.
     * This method decodes the string, which is expected to be Base64 encoded, into a byte array.
     * It then uses the {@link KeyFactory} with the RSA algorithm to generate a {@link PublicKey} from the specified key specification.
     *
     * @param publicKeyStr The Base64 encoded string representation of the public key.
     * @return A {@link PublicKey} object generated from the provided string.
     * @throws NoSuchAlgorithmException If the RSA algorithm is not supported by the {@link KeyFactory}.
     * @throws InvalidKeySpecException  If the provided key specification is inappropriate for initializing the {@link KeyFactory}.
     */
    @Transactional
    public PublicKey convertStringToPublicKey(String publicKeyStr) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] publicBytes = Base64.getDecoder().decode(publicKeyStr);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    /**
     * Converts a Base64 encoded string representation of a private key into a {@link PrivateKey} object.
     * This method decodes the Base64 encoded string into a byte array, then uses a {@link KeyFactory}
     * with the RSA algorithm to generate a {@link PrivateKey} from the specified PKCS8 encoded key specification.
     *
     * @param privateKeyStr The Base64 encoded string representation of the private key.
     * @return A {@link PrivateKey} object generated from the provided string.
     * @throws NoSuchAlgorithmException If the RSA algorithm is not supported by the {@link KeyFactory}.
     * @throws InvalidKeySpecException  If the provided key specification is inappropriate for initializing the {@link KeyFactory}.
     */
    @Transactional
    public PrivateKey convertStringToPrivateKey(String privateKeyStr) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] privateBytes = Base64.getDecoder().decode(privateKeyStr);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * Validates a digital signature against the provided data using a public key.
     * This method initializes a {@link Signature} instance for the SHA256withRSA algorithm,
     * then verifies the provided digital signature against the given data using the specified public key.
     *
     * @param signatureBytes The digital signature to be verified, as a byte array.
     * @param publicKey      The public key used for verification, as a {@link PublicKey} object.
     * @param data           The data against which the digital signature is to be verified, as a byte array.
     * @return {@code true} if the digital signature is valid and matches the provided data using the public key; {@code false} otherwise.
     * @throws NoSuchAlgorithmException If the SHA256withRSA algorithm is not available.
     * @throws SignatureException       If an error occurs during the signature verification process.
     * @throws InvalidKeyException      If the provided public key is invalid.
     */
    @Transactional
    public boolean isSignatureValid(byte[] signatureBytes, PublicKey publicKey, byte[] data) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(data);
        return signature.verify(signatureBytes);
    }

    /**
     * Creates a digital signature for a contract using a private key.
     * This method serializes the contract into a ContractDTO, converts it into a byte array,
     * and then signs this data using the provided private key. The signature is generated using the SHA256withRSA
     * algorithm, which ensures the integrity and authenticity of the contract data. The generated signature
     * is then encoded into a Base64 string to ensure safe transmission and storage.
     *
     * @param contract   The contract entity to be signed. It must not be null.
     * @param privateKey The private key used to sign the contract data. It must be a valid RSA private key.
     * @return A Base64 encoded string representing the digital signature of the contract.
     * @throws NoSuchAlgorithmException If the SHA256withRSA algorithm is not available in the environment.
     * @throws InvalidKeyException      If the provided private key is invalid.
     * @throws SignatureException       If an error occurs during the signing process.
     * @throws JsonProcessingException  If an error occurs during the serialization of the ContractDTO.
     */
    @Transactional
    public String createContractSignature(Contract contract, PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, JsonProcessingException {
        ContractDTO contractDTO = convertContractToDTO(contract);
        byte[] dataBytes = prepareContractDataBytes(contractDTO);
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(dataBytes);
        byte[] signatureBytes = signature.sign();
        return Base64.getEncoder().encodeToString(signatureBytes);
    }


    /**
     * Handles the withdrawal process.
     *
     * @param withdrawalId   the ID of the withdrawal to be processed
     * @param actionStr      the action to be performed on the withdrawal. It can be either "CONFIRM_WITHDRAWAL" or "COMPLETE_WITHDRAWAL"
     * @param withdrawQrCode the QR code associated with the withdrawal. This parameter is only required when the action is "COMPLETE_WITHDRAWAL"
     * @return a {@link WithdrawGoldDefault} object containing the details of the withdrawal after the specified action is performed
     * @throws IllegalArgumentException      if the actionStr parameter is not a valid withdrawal action
     * @throws NotFoundException             if the specified withdrawal does not exist
     * @throws WithdrawalNotAllowedException if the specified action cannot be performed on the specified withdrawal
     */
    @Transactional
    public WithdrawGoldDefault handleWithdrawal(Long withdrawalId, String actionStr, String withdrawQrCode) throws Exception{
        WithdrawalAction action;
        try {
            action = WithdrawalAction.valueOf(actionStr.toUpperCase());

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Not supported " + actionStr + " action", e);
        }
        return switch (action) {
            case CONFIRM_WITHDRAWAL -> confirmWithdrawal(withdrawalId);
            case COMPLETE_WITHDRAWAL -> completeWithdrawByQrCode(withdrawQrCode);
        };
    }


    /**
     * Updates the user's balance by adding the specified amount.
     *
     * @param userInfo the user information object
     * @param amount   the amount to be added to the user's balance
     */
    private void updateUserBalance(UserInfo userInfo, BigDecimal amount) {
        Balance balance = userInfo.getBalance();
        balance.setBalance(balance.getBalance().add(amount));
        userInfoRepository.save(userInfo);
    }


    /**
     * Updates the inventories of gold in the system.
     *
     * @param goldInventory the gold inventory object
     * @param userInventory the user gold inventory object
     * @param quantityInOz  the quantity of gold in ounces
     * @param isBuying      a boolean value indicating whether the transaction is a buy or sell
     */
    private void updateInventories(GoldInventory goldInventory, UserGoldInventory userInventory,
                                   BigDecimal quantityInOz, boolean isBuying) {
        //     BigDecimal quantityInKg = quantityInOz.divide(new BigDecimal("31.1034768"), 2, RoundingMode.HALF_UP);
        if (isBuying) {
            goldInventory.setTotalWeightKg(goldInventory.getTotalWeightKg().subtract(quantityInOz));

            userInventory.setTotalWeightOz(userInventory.getTotalWeightOz().add(quantityInOz));
        } else {
            goldInventory.setTotalWeightKg(goldInventory.getTotalWeightKg().add(quantityInOz));

            userInventory.setTotalWeightOz(userInventory.getTotalWeightOz().subtract(quantityInOz));
        }
        goldInventoryRepository.save(goldInventory);
        userInventoryRepository.save(userInventory);
    }


    /**
     * Records a new contract for a given user, quantity, price, and transaction type.
     *
     * @param userInfo     the user for whom the contract is being recorded
     * @param quantityInOz the quantity of gold in ounces for the contract
     * @param pricePerOz   the price per ounce of gold for the contract
     * @param amount       the total cost or profit for the contract
     * @param type         the type of transaction (BUY or SELL)
     * @param goldUnit     the unit of measurement for the gold (TROY_OUNCE or GRAM)
     * @param signature    the signature file containing the transaction signature
     * @return a ContractDTO object containing the details of the newly recorded contract
     * @throws IOException       if an error occurs while reading the signature file
     * @throws NotFoundException if the user or organization signature is not found
     */
    @Transactional
    public ContractDTO recordContract(UserInfo userInfo, BigDecimal quantityInOz, BigDecimal pricePerOz,
                                      BigDecimal amount, TransactionType type, GoldUnit goldUnit, MultipartFile signature, GoldTransaction transaction) throws IOException {
        Contract contract = new Contract();
        contract.setActionParty(userInfo);
        contract.setActionPartyFullName(userInfo.getFirstName() + " " + userInfo.getLastName());
        contract.setActionPartyAddress(userInfo.getAddress());
        contract.setQuantity(quantityInOz);
        contract.setPricePerOunce(pricePerOz);
        contract.setTotalCostOrProfit(amount);
        contract.setTransactionType(type);
        contract.setCreatedAt(Date.from(Instant.now()));
        contract.setConfirmingParty("BGSS Company");
        contract.setGoldUnit(goldUnit);
        if (signature == null || signature.isEmpty()) {
            contract.setSignatureActionParty(null);
        } else {
            String base64Signature = Base64.getEncoder().encodeToString(signature.getBytes());
            contract.setSignatureActionParty(base64Signature);
        }
        OrganizationSignature organizationSignature = organizationSignatureRepository.findOrganizationSignatureById(1L);
        contract.setSignatureConfirmingParty(organizationSignature.getSignature());
        contract.setContractStatus(ContractStatus.DIGITAL_UNSIGNED);
        contract.setTransaction(transaction);
        contractRepository.save(contract);
        return new ContractDTO(
                contract.getId(),
                contract.getActionParty().getId(),
                contract.getActionPartyFullName(),
                contract.getActionPartyAddress(),
                contract.getQuantity(),
                contract.getPricePerOunce(),
                contract.getTotalCostOrProfit(),
                contract.getTransactionType(),
                contract.getCreatedAt(),
                contract.getConfirmingParty(),
                contract.getGoldUnit(),
                contract.getSignatureActionParty(),
                contract.getSignatureConfirmingParty(),
                contract.getContractStatus()
        );
    }

    /**
     * Records a new transaction for a given user, quantity, price, and transaction type.
     *
     * @param userInfo     the user for whom the transaction is being recorded
     * @param quantityInOz the quantity of gold in ounces for the transaction
     * @param pricePerOz   the price per ounce of gold for the transaction
     * @param amount       the total cost or profit for the transaction
     * @param type         the type of transaction (BUY or SELL)
     * @param goldUnit     the unit of measurement for the gold (TROY_OUNCE or GRAM)
     * @return a TransactionDTO object containing the details of the newly recorded transaction
     * @throws NotFoundException     if the user or organization signature is not found
     * @throws IllegalStateException if there is not enough gold in the inventory to complete the transaction
     */
    private TransactionDTO recordTransaction(UserInfo userInfo, BigDecimal quantityInOz, BigDecimal pricePerOz,
                                             BigDecimal amount, TransactionType type, GoldUnit goldUnit) throws NotFoundException, IllegalStateException, IOException, MessagingException {
        GoldTransaction transaction = new GoldTransaction();
        transaction.setActionParty(userInfo);
        transaction.setQuantity(quantityInOz);
        transaction.setPricePerOunce(pricePerOz);
        transaction.setTotalCostOrProfit(type == TransactionType.BUY ? amount.negate() : amount);
        transaction.setTransactionType(type);
        transaction.setCreatedAt(Instant.now());
        transaction.setConfirmingParty("BGSS Company");
        transaction.setGoldUnit(goldUnit);

        transaction.setTransactionVerification(TransactionVerification.UNVERIFIED);
        transaction.setTransactionSignature(TransactionSignature.UNSIGNED);
        transaction.setTransactionStatus(TransactionStatus.PENDING);
        goldTransactionRepository.save(transaction);
//        mailAuthentication.generateOtpForTransaction(transaction);
        return new TransactionDTO(transaction.getId(),
                transaction.getQuantity(),
                transaction.getPricePerOunce(),
                transaction.getTotalCostOrProfit(),
                transaction.getTransactionType(),
                transaction.getCreatedAt(),
                transaction.getActionParty().getId(),
                transaction.getGoldUnit(),
                transaction.getConfirmingParty(),
                transaction.getTransactionVerification(),
                transaction.getTransactionSignature(),
                transaction.getTransactionStatus(),
                null);
    }


    /**
     * This method is used to request a withdrawal.
     *
     * @param userInfoId the id of the user info
     * @param weight     the weight of the gold to be withdrawn
     * @param unit       the unit of measurement for the gold (TROY_OUNCE or GRAM)
     * @return a WithdrawGoldDefault object containing the details of the requested withdrawal
     * @throws WithdrawalNotAllowedException if the user info id is not found or if the user's gold inventory is not sufficient to withdraw the requested amount
     */
    @Transactional
    public WithdrawGoldDefault requestWithdrawal(Long userInfoId, BigDecimal weight, GoldUnit unit, WithdrawRequirement withdrawRequirement, Long productId) throws MessagingException, IOException {
        UserGoldInventory inventory = userInventoryRepository.findByUserInfoId(userInfoId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        if (withdrawRequirement == WithdrawRequirement.CRAFT) {
            BigDecimal weightToWithdrawInOz = unit.convertToTroyOunce(weight);
            if (inventory.getTotalWeightOz().compareTo(weightToWithdrawInOz) < 0) {
                throw new WithdrawalNotAllowedException("The gold in inventory is not enough to withdraw");
            }
        } else if (withdrawRequirement == WithdrawRequirement.AVAILABLE) {
            BigDecimal weightToWithdrawInOzAvailable = unit.convertToTroyOunce(BigDecimal.valueOf(product.getWeight()));
            if (inventory.getTotalWeightOz().compareTo(weightToWithdrawInOzAvailable) < 0) {
                throw new WithdrawalNotAllowedException("The gold in inventory is not enough to withdraw");
            }
//            inventory.setTotalWeightOz(inventory.getTotalWeightOz().subtract(weightToWithdrawInOzAvailable));
//            userInventoryRepository.save(inventory);
        }

        WithdrawGold withdrawal = new WithdrawGold();
        withdrawal.setUserInfo(inventory.getUserInfo());
//        withdrawal.setAmount(weight);
        withdrawal.setStatus(WithdrawalStatus.UNVERIFIED);
        withdrawal.setTransactionDate(Instant.now());
        withdrawal.setUpdateDate(Instant.now());
        withdrawal.setUserInventory(inventory);
        withdrawal.setGoldUnit(unit);
        withdrawal.setWithdrawQrCode(blockchainServer.randomCode(8));
        withdrawal.setUserConfirm(EUserConfirmWithdraw.NOT_RECEIVED);
        withdrawal.setWithdrawRequirement(withdrawRequirement);

        ProductDTOs productDTO = switch (withdrawRequirement) {
            case AVAILABLE -> handleAvailableWithdrawal(productId, withdrawal, withdrawRequirement);
            case CRAFT -> handleCraftWithdrawal(productId, withdrawal, weight.doubleValue(), withdrawRequirement);
        };

        withdrawGoldRepository.save(withdrawal);
        mailAuthentication.generateOtpForWithdraw(withdrawal);

        return new WithdrawGoldDefault(
                withdrawal.getWithdrawQrCode(),
                withdrawal.getAmount(),
                withdrawal.getStatus(),
                withdrawal.getTransactionDate(),
                withdrawal.getUpdateDate(),
                withdrawal.getUserInfo().getId(),
                withdrawal.getGoldUnit(),
                withdrawal.getUserConfirm(),
                productDTO,
                withdrawal.getId(),
                withdrawal.getWithdrawRequirement());
    }

    public ProductDTOs handleAvailableWithdrawal(Long productId, WithdrawGold withdrawal, WithdrawRequirement withdrawRequirement) {
        Product availableProduct = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Not found product"));
        if (!availableProduct.getTypeGold().getTypeName().equals("24k gold") || availableProduct.getTypeGold() == null) {
            throw new IllegalStateException("Type Gold is not supported! Cause only 24K gold is supported");
        }
        withdrawal.setProduct(availableProduct);
        withdrawal.setAmount(availableProduct.getWeight());
        withdrawal.setWithdrawRequirement(withdrawRequirement);
        return mapToProductDTO(availableProduct);
    }

    public ProductDTOs handleCraftWithdrawal(Long productId, WithdrawGold withdrawal, Double weight, WithdrawRequirement withdrawRequirement) {
        Product craftProducts = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Not found product"));
        withdrawal.setProduct(craftProducts);
        withdrawal.setAmount(weight);
        withdrawal.setWithdrawRequirement(withdrawRequirement);
        return mapToProductDTO(craftProducts);
    }

    private ProductDTOs mapToProductDTO(Product product) {
        if (product == null) {
            return null;
        }

        List<ProductImagesDTO> productImagesDTOS = null;
        if (product.getProductImages() != null) {
            productImagesDTOS = product.getProductImages().stream()
                    .map(this::mapToProductImagesDTO)
                    .collect(Collectors.toList());
        }

        BigDecimal priceProduct = BigDecimal.ZERO;
        if(product.getTypeGoldOption().equals(EGoldOptionType.AVAILABLE)){
            priceProduct = product.getTypeGold().getPrice()
                    .multiply(BigDecimal.valueOf(product.getWeight()))
                    .add(product.getProcessingCost());
        }

        return new ProductDTOs(
                product.getId(),
                product.getProductName(),
                product.getDescription(),
                product.getUnitOfStock(),
                productImagesDTOS,
                product.getCategory(),
                product.getTypeGold(),
                product.getAvgReview(),
                product.getTypeGoldOption(),
                product.isActive(),
                priceProduct

        );
    }

    private ProductImagesDTO mapToProductImagesDTO(ProductImage productImage) {
        ProductImagesDTO productImagesDTO = new ProductImagesDTO();
        productImagesDTO.setId(productImage.getId());
        productImagesDTO.setImgUrl(productImage.getImgUrl());
        return productImagesDTO;
    }

    /**
     * This method is used to confirm a withdrawal request.
     *
     * @param withdrawalId the id of the withdrawal request
     * @return a WithdrawGoldDefault object containing the details of the confirmed withdrawal
     * @throws NotFoundException             if the withdrawal request with the given id is not found
     * @throws WithdrawalNotAllowedException if the withdrawal request is already confirmed or completed
     */
    @Transactional
    public WithdrawGoldDefault confirmWithdrawal(Long withdrawalId) {
        WithdrawGold withdrawal = withdrawGoldRepository.findById(withdrawalId)
                .orElseThrow(() -> new NotFoundException("Withdrawal with ID " + withdrawalId + " not found."));
        if (withdrawal.getStatus() == WithdrawalStatus.COMPLETED) {
            throw new WithdrawalNotAllowedException("Access Denied! Cause: The status cannot be changed once completed");
        }
        if (withdrawal.getStatus() == WithdrawalStatus.CONFIRMED) {
            throw new WithdrawalNotAllowedException("Access Denied! Cause: The status cannot be changed once confirmed");
        }

        BigDecimal convert = withdrawal.getGoldUnit().convertToTroyOunce(BigDecimal.valueOf(withdrawal.getAmount()));
        UserGoldInventory inventory = userInventoryRepository.findByUserInfoId(withdrawal.getUserInfo().getId());
        if (inventory.getTotalWeightOz().compareTo(convert) < 0) {
            throw new WithdrawalNotAllowedException("The gold in inventory is not enough to withdraw");
        }
        inventory.setTotalWeightOz(inventory.getTotalWeightOz().subtract(convert));
        userInventoryRepository.save(inventory);
        withdrawal.setStatus(WithdrawalStatus.CONFIRMED);
        withdrawal.setUpdateDate(Instant.now());
        withdrawGoldRepository.save(withdrawal);

        ProductDTOs productDTO = mapToProductDTO(withdrawal.getProduct());

        return new WithdrawGoldDefault(
                withdrawal.getWithdrawQrCode(),
                withdrawal.getAmount(),
                withdrawal.getStatus(),
                withdrawal.getTransactionDate(),
                withdrawal.getUpdateDate(),
                withdrawal.getUserInfo().getId(),
                withdrawal.getGoldUnit(),
                withdrawal.getUserConfirm(),
                productDTO,
                withdrawal.getId(),
                withdrawal.getWithdrawRequirement());

    }


    /**
     * This method is used to complete a withdrawal request.
     *
     * @param withdrawQrCode the QR code associated with the withdrawal request
     * @return a WithdrawGoldDefault object containing the details of the completed withdrawal
     * @throws NotFoundException             if the withdrawal request with the given QR code is not found
     * @throws WithdrawalNotAllowedException if the withdrawal request is already completed
     */
    @Transactional
    public WithdrawGoldDefault completeWithdrawByQrCode(String withdrawQrCode) {
        WithdrawGold withdrawGoldById = withdrawGoldRepository.findByQrCode(withdrawQrCode)
                .orElseThrow(() -> new NotFoundException("Not Found withdraw with QR Code: " + withdrawQrCode));
        if (withdrawGoldById.getStatus() == WithdrawalStatus.COMPLETED) {
            throw new WithdrawalNotAllowedException("Access Denied! Cause: The status cannot be changed once completed");
        }
        if (withdrawGoldById.getStatus() != WithdrawalStatus.CONFIRMED) {
            throw new WithdrawalNotAllowedException("Access Denied! Cause: Withdrawal must be confirmed before it can be completed");
        }

        withdrawGoldById.setStatus(WithdrawalStatus.COMPLETED);

        withdrawGoldById.setUpdateDate(Instant.now());

        withdrawGoldRepository.save(withdrawGoldById);

        ProductDTOs productDTO = mapToProductDTO(withdrawGoldById.getProduct());

        return new WithdrawGoldDefault(
                withdrawGoldById.getWithdrawQrCode(),
                withdrawGoldById.getAmount(),
                withdrawGoldById.getStatus(),
                withdrawGoldById.getTransactionDate(),
                withdrawGoldById.getUpdateDate(),
                withdrawGoldById.getUserInfo().getId(),
                withdrawGoldById.getGoldUnit(),
                withdrawGoldById.getUserConfirm(),
                productDTO,
                withdrawGoldById.getId(),
                withdrawGoldById.getWithdrawRequirement());
    }


    /**
     * This method is used to complete a withdrawal request.
     *
     * @param withdrawalId the id of the withdrawal request
     * @return a WithdrawGoldDefault object containing the details of the completed withdrawal
     * @throws NotFoundException             if the withdrawal request with the given id is not found
     * @throws WithdrawalNotAllowedException if the withdrawal request is already completed
     */
    @Transactional
    public WithdrawGoldDefault completeWithdrawal(Long withdrawalId) {

        WithdrawGold withdrawal = withdrawGoldRepository.findById(withdrawalId)
                .orElseThrow(() -> new NotFoundException("Withdrawal with ID " + withdrawalId + " not found."));

        if (withdrawal.getStatus() == WithdrawalStatus.COMPLETED) {
            throw new WithdrawalNotAllowedException("Access Denied! Cause: The status cannot be changed once completed");
        }

        withdrawal.setStatus(WithdrawalStatus.COMPLETED);
        withdrawal.setUpdateDate(Instant.now());
        withdrawGoldRepository.save(withdrawal);

        ProductDTOs productDTO = mapToProductDTO(withdrawal.getProduct());

        return new WithdrawGoldDefault(
                withdrawal.getWithdrawQrCode(),
                withdrawal.getAmount(),
                withdrawal.getStatus(),
                withdrawal.getTransactionDate(),
                withdrawal.getUpdateDate(),
                withdrawal.getUserInfo().getId(),
                withdrawal.getGoldUnit(),
                withdrawal.getUserConfirm(),
                productDTO,
                withdrawal.getId(),
                withdrawal.getWithdrawRequirement());
    }


//    /**
//     * This method is used to complete a withdrawal request.
//     *
//     * @param withdrawalId the id of the withdrawal request
//     * @return a WithdrawGoldDefault object containing the details of the completed withdrawal
//     * @throws NotFoundException             if the withdrawal request with the given id is not found
//     * @throws WithdrawalNotAllowedException if the withdrawal request is already completed
//     */
//    @Transactional
//    public WithdrawGoldDefault completeWithdrawal(Long withdrawalId) {
//
//        WithdrawGold withdrawal = withdrawGoldRepository.findById(withdrawalId)
//                .orElseThrow(() -> new NotFoundException("Withdrawal with ID " + withdrawalId + " not found."));
//
//        if (withdrawal.getStatus() == WithdrawalStatus.COMPLETED) {
//            throw new WithdrawalNotAllowedException("Access Denied! Cause: The status cannot be changed once completed");
//        }
//
//        withdrawal.setStatus(WithdrawalStatus.COMPLETED);
//        withdrawal.setUpdateDate(Instant.now());
//        withdrawGoldRepository.save(withdrawal);
//
//        return new WithdrawGoldDefault(withdrawal.getId(),
//                withdrawal.getWithdrawQrCode(),
//                withdrawal.getAmount(),
//                withdrawal.getStatus(),
//                withdrawal.getTransactionDate(),
//                withdrawal.getUpdateDate(),
//                withdrawal.getUserInfo().getId(),
//                withdrawal.getGoldUnit());
//
//    }



    /**
     * This method is used to cancel a withdrawal request.
     *
     * @param withdrawalId       the id of the withdrawal request
     * @param cancellationReason the reason for cancellation
     * @param username           the username of the current user
     * @return a WithdrawGoldDTO object containing the details of the cancelled withdrawal
     * @throws NotFoundException             if the withdrawal request with the given id is not found
     * @throws WithdrawalNotAllowedException if the withdrawal request is already completed or cancelled
     */
    @Transactional
    public WithdrawGoldDTO cancelWithdrawal(Long withdrawalId, String cancellationReason, String username) {

        User currentUser = userRepository.findByUsername(username).
                orElseThrow(() -> new NotFoundException("Username: " + username + "not found"));

        boolean isCurrentUserAdmin = currentUser.getRoleNames().contains("ROLE_ADMIN");

        WithdrawGold withdrawal = withdrawGoldRepository.findById(withdrawalId).
                orElseThrow(() -> new NotFoundException("Withdrawal with ID " + withdrawalId + " not found."));

        Optional<Product> product = productRepository.findById(withdrawal.getProduct().getId());

//        if (withdrawal.getStatus() == WithdrawalStatus.COMPLETED) {
//            throw new WithdrawalNotAllowedException("Access Denied! Cause: The status cannot be changed once completed");
//        }

        if (withdrawal.getStatus() == WithdrawalStatus.CANCELLED) {
            throw new WithdrawalNotAllowedException("Access Denied! Cause: The status cannot be changed once canceled");
        }

        if(withdrawal.getUserConfirm() == EUserConfirmWithdraw.RECEIVED) {
            throw new WithdrawalNotAllowedException("Access Denied! Cause: The status cannot be changed once confirmed");
        }

        CancellationMessage cancellationMessage = new CancellationMessage();

        cancellationMessage.setReason(cancellationReason);
        cancellationMessage.setWithdrawal(withdrawal);

        cancellationMessage.setSender(currentUser.getUsername());
        cancellationMessage.setReceiver(isCurrentUserAdmin ? withdrawal.getUserInfo().getUser().getUsername() : "admin");

        cancellationMessageRepository.save(cancellationMessage);

//        if (withdrawal.getStatus() == WithdrawalStatus.PENDING || withdrawal.getStatus() == WithdrawalStatus.CONFIRMED ||
//            withdrawal.getStatus() == WithdrawalStatus.UNVERIFIED || withdraw.getStatus() == WithdrawalStatus.CONFIRMED) {

            if (withdrawal.getStatus() == WithdrawalStatus.APPROVED) {
                BigDecimal convert = withdrawal.getGoldUnit().convertToTroyOunce(BigDecimal.valueOf(withdrawal.getAmount()));
                UserGoldInventory inventory = userInventoryRepository.findByUserInfoId(withdrawal.getUserInfo().getId());
                inventory.setTotalWeightOz(inventory.getTotalWeightOz().add(convert));
                userInventoryRepository.save(inventory);
                product.get().setUnitOfStock(product.get().getUnitOfStock() + 1);
                productRepository.save(product.get());
            }
            withdrawal.setStatus(WithdrawalStatus.CANCELLED);
            withdrawal.setUpdateDate(Instant.now());
            withdrawGoldRepository.save(withdrawal);
//        }

        Set<CancellationMessageDTO> cancellationMessageDTOs = withdrawal.getCancellationMessages().stream()
                .map(CancellationMessageDTO::new)
                .collect(Collectors.toSet());

        ProductDTOs productDTO = mapToProductDTO(withdrawal.getProduct());

        return new WithdrawGoldDTO(withdrawal.getId(),
                withdrawal.getWithdrawQrCode(),
                withdrawal.getAmount(),
                withdrawal.getStatus(),
                withdrawal.getTransactionDate(),
                withdrawal.getUserInfo().getId(),
                withdrawal.getUpdateDate(),
                withdrawal.getGoldUnit(),
                withdrawal.getUserConfirm(),
                cancellationMessageDTOs,
                productDTO
        );

    }

    public boolean userConfirmReceived(Long withdrawId){

            WithdrawGold withdrawGold = withdrawGoldRepository.findById(withdrawId).orElseThrow(() -> new NotFoundException("Not found order with id: " + withdrawId));
            if(withdrawGold.getStatus().equals(WithdrawalStatus.APPROVED) && withdrawGold.getUserConfirm().equals(EUserConfirmWithdraw.RECEIVED)){
                throw new UserConfirmWithdrawException("Customers cannot confirm after the withdrawal request is completed on both sides.");
            }

            if(withdrawGold.getStatus().equals(WithdrawalStatus.APPROVED)){
                withdrawGold.setUserConfirm(EUserConfirmWithdraw.RECEIVED);
                withdrawGold.setStatus(WithdrawalStatus.COMPLETED);
            }
            else{
                throw new UserConfirmWithdrawException("The customer only confirm received gold when request withdraw was be set complete by admin or staff.");
            }

            withdrawGoldRepository.save(withdrawGold);
            return true;
    }
}