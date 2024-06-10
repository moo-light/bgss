package com.server.blockchainserver.platform.server;

import com.server.blockchainserver.dto.gold_inventory_dto.CancellationMessageDTO;
import com.server.blockchainserver.dto.product_dto.ProductDTO;
import com.server.blockchainserver.dto.product_dto.ProductDTOs;
import com.server.blockchainserver.dto.product_dto.ProductImagesDTO;
import com.server.blockchainserver.exeptions.NotFoundException;
import com.server.blockchainserver.models.enums.EGoldOptionType;
import com.server.blockchainserver.models.shopping_model.Product;
import com.server.blockchainserver.models.shopping_model.ProductImage;
import com.server.blockchainserver.platform.data_transfer_object.ContractDTO;
import com.server.blockchainserver.platform.data_transfer_object.TransactionDTO;
import com.server.blockchainserver.platform.data_transfer_object.WithdrawGoldDTO;
import com.server.blockchainserver.platform.entity.CancellationMessage;
import com.server.blockchainserver.platform.entity.Contract;
import com.server.blockchainserver.platform.entity.GoldTransaction;
import com.server.blockchainserver.platform.entity.WithdrawGold;
import com.server.blockchainserver.platform.repositories.GoldTransactionRepository;
import com.server.blockchainserver.platform.repositories.WithdrawGoldRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class TransactionManagement {

    @Autowired
    private GoldTransactionRepository goldTransactionRepository;
    @Autowired
    private WithdrawGoldRepository withdrawGoldRepository;


    /**
     * Retrieves a list of all gold transactions sorted in descending order by creation date.
     * <p>
     * This method queries the database for all instances of {@link GoldTransaction} using the {@link GoldTransactionRepository}.
     * The results are sorted in descending order based on the 'createdAt' field, ensuring the most recent transactions are listed first.
     *
     * @return A list of {@link GoldTransaction} objects representing all gold transactions, sorted by creation date in descending order.
     */
    public List<TransactionDTO> transactionList() {
        List<GoldTransaction> transactions = goldTransactionRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        return transactions.stream().map(this::convertToTransactionDTO).collect(Collectors.toList());

    }


    /**
     * Retrieves a list of gold transactions for a specific user, sorted in descending order by creation date.
     * <p>
     * This method queries the database for instances of {@link GoldTransaction} associated with the given user ID.
     * The results are sorted in descending order based on the 'createdAt' field, ensuring the most recent transactions
     * are listed first. This allows for an easy retrieval of a user's transaction history in chronological order.
     *
     * @param userInfoId The ID of the user for whom the transaction list is being retrieved.
     * @return A list of {@link GoldTransaction} objects representing the transactions associated with the specified user,
     * sorted by creation date in descending order.transactionListUser
     */
    public List<TransactionDTO> transactionListUser(Long userInfoId) {
        List<GoldTransaction> transactions = goldTransactionRepository.findByActionPartyId(userInfoId, Sort.by(Sort.Direction.DESC, "createdAt"));
        return transactions.stream().map(this::convertToTransactionDTO).collect(Collectors.toList());
    }


    /**
     * Retrieves a list of all withdrawals in the form of {@link WithdrawGoldDTO}, sorted in descending order by transaction date.
     * <p>
     * This method queries the database for all instances of {@link WithdrawGold} using the {@link WithdrawGoldRepository}.
     * Each {@link WithdrawGold} entity is then converted to a {@link WithdrawGoldDTO} to encapsulate the withdrawal data transfer object.
     * The results are sorted in descending order based on the 'transactionDate' field, ensuring the most recent withdrawals are listed first.
     * This allows for an easy retrieval and display of withdrawal history in chronological order.
     *
     * @return A list of {@link WithdrawGoldDTO} objects representing all withdrawals, sorted by transaction date in descending order.
     */
    public List<WithdrawGoldDTO> withdrawGoldDTOList() {
        List<WithdrawGold> withdrawGolds = withdrawGoldRepository.findAll(
                Sort.by(Sort.Direction.DESC, "transactionDate"));
        return withdrawGolds.stream().map(this::convertToWithdrawDTO).collect(Collectors.toList());
    }

    /**
     * Retrieves a list of withdrawal transactions for a specific user, sorted in descending order by transaction date.
     * <p>
     * This method queries the database for instances of {@link WithdrawGold} associated with the given user ID.
     * The results are then converted to {@link WithdrawGoldDTO} to encapsulate the withdrawal data transfer object.
     * The withdrawals are sorted in descending order based on the 'transactionDate' field, ensuring the most recent
     * withdrawals are listed first. This facilitates an easy retrieval and display of a user's withdrawal history
     * in chronological order.
     *
     * @param userInfoId The ID of the user for whom the withdrawal list is being retrieved.
     * @return A list of {@link WithdrawGoldDTO} objects representing the withdrawals associated with the specified user,
     * sorted by transaction date in descending order.
     */
    public List<WithdrawGoldDTO> withdrawGoldDTOListUserInfo(Long userInfoId) {
        List<WithdrawGold> withdrawGolds = withdrawGoldRepository.findByUserInfoId(userInfoId,
                Sort.by(Sort.Direction.DESC, "transactionDate"));
        return withdrawGolds.stream().map(this::convertToWithdrawDTO).collect(Collectors.toList());
    }

    /**
     * Retrieves a {@link WithdrawGoldDTO} object for a specific withdrawal identified by its QR code.
     * <p>
     * This method queries the database for an instance of {@link WithdrawGold} using the provided QR code.
     * If a matching withdrawal is found, it is converted to a {@link WithdrawGoldDTO} to encapsulate the withdrawal data.
     * If no withdrawal is found matching the QR code, a {@link NotFoundException} is thrown.
     *
     * @param qrCode The QR code associated with the withdrawal to be retrieved.
     * @return A {@link WithdrawGoldDTO} object representing the withdrawal associated with the specified QR code.
     * @throws NotFoundException if no withdrawal is found with the provided QR code.
     */
    public WithdrawGoldDTO getWithdrawGoldByQrCode(String qrCode) {
        WithdrawGold withdrawGold = withdrawGoldRepository.findByQrCode(qrCode).orElseThrow(() -> new NotFoundException("Withdraw not found!"));
        return convertToWithdrawDTO(withdrawGold);
    }


    /**
     * Converts a {@link GoldTransaction} entity to a {@link TransactionDTO} data transfer object.
     * <p>
     * This method maps the properties of a {@link GoldTransaction} entity to a new {@link TransactionDTO} object.
     * It extracts and sets the relevant information such as transaction ID, action party ID, quantity, price per ounce,
     * gold unit, total cost or profit, transaction type, creation date, and confirming party. This conversion is necessary
     * for transferring transaction data in a simplified form, especially when communicating between different parts of the application
     * or when exposing transaction data to the client side.
     * </p>
     *
     * @param transaction The {@link GoldTransaction} entity to be converted.
     * @return A {@link TransactionDTO} object containing the simplified transaction data.
     */
    private TransactionDTO convertToTransactionDTO(GoldTransaction transaction) {

        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setId(transaction.getId());
        transactionDTO.setActionPartyId(transaction.getActionParty().getId()); // Lấy chỉ ID
        transactionDTO.setQuantity(transaction.getQuantity());
        transactionDTO.setPricePerOunce(transaction.getPricePerOunce());
        transactionDTO.setGoldUnit(transaction.getGoldUnit());
        transactionDTO.setTotalCostOrProfit(transaction.getTotalCostOrProfit());
        transactionDTO.setTransactionType(transaction.getTransactionType());
        transactionDTO.setCreatedAt(transaction.getCreatedAt());
        transactionDTO.setConfirmingParty(transaction.getConfirmingParty());
        transactionDTO.setTransactionVerification(transaction.getTransactionVerification());
        transactionDTO.setTransactionSignature(transaction.getTransactionSignature());
        transactionDTO.setTransactionStatus(transaction.getTransactionStatus());
        transactionDTO.setContract(convertToContractDTO(transaction.getContract()));

        return transactionDTO;
    }

    private ContractDTO convertToContractDTO(Contract contract) {
        if (contract == null) {
            return null;
        }
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
     * Converts a {@link WithdrawGold} entity to a {@link WithdrawGoldDTO} data transfer object.
     * <p>
     * This method maps the properties of a {@link WithdrawGold} entity to a new {@link WithdrawGoldDTO} object.
     * It includes the withdrawal ID, QR code, amount, status, transaction date, user ID, update date, and gold unit.
     * Additionally, it converts each associated {@link CancellationMessage} entity into a {@link CancellationMessageDTO}
     * and collects them into a set. This conversion is essential for transferring withdrawal data in a simplified form,
     * especially when communicating between different parts of the application or when exposing withdrawal data to the client side.
     * </p>
     *
     * @param withdrawGold The {@link WithdrawGold} entity to be converted.
     * @return A {@link WithdrawGoldDTO} object containing the simplified withdrawal data.
     */
    private WithdrawGoldDTO convertToWithdrawDTO(WithdrawGold withdrawGold) {
        WithdrawGoldDTO withdrawGoldDTO = new WithdrawGoldDTO();
        withdrawGoldDTO.setId(withdrawGold.getId());
        withdrawGoldDTO.setWithdrawQrCode(withdrawGold.getWithdrawQrCode());
        withdrawGoldDTO.setAmount(withdrawGold.getAmount());
        withdrawGoldDTO.setStatus(withdrawGold.getStatus());
        withdrawGoldDTO.setTransactionDate(withdrawGold.getTransactionDate());
        withdrawGoldDTO.setUserInfoId(withdrawGold.getUserInfo().getId());
        withdrawGoldDTO.setUpdateDate(withdrawGold.getUpdateDate());
        withdrawGoldDTO.setGoldUnit(withdrawGold.getGoldUnit());
        withdrawGoldDTO.setUserConfirm(withdrawGold.getUserConfirm());
        withdrawGoldDTO.setProductDTO(mapToProductDTO(withdrawGold.getProduct()));

        // Convert each CancellationMessage entity to CancellationMessageDTO
        Set<CancellationMessageDTO> cancellationMessageDTOs = withdrawGold.getCancellationMessages().stream()
                .map(cancellationMessage -> new CancellationMessageDTO(
                        cancellationMessage.getId(),
                        cancellationMessage.getSender(),
                        cancellationMessage.getReceiver(),
                        cancellationMessage.getReason(),
                        cancellationMessage.getWithdrawal().getId()))
                .collect(Collectors.toSet());

        // Set the converted CancellationMessageDTOs
        withdrawGoldDTO.setCancellationMessages(cancellationMessageDTOs);

        return withdrawGoldDTO;
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
}
