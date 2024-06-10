package com.server.blockchainserver.platform.entity;

import com.server.blockchainserver.models.user_model.UserInfo;
import com.server.blockchainserver.platform.entity.enums.ContractStatus;
import com.server.blockchainserver.platform.entity.enums.GoldUnit;
import com.server.blockchainserver.platform.entity.enums.TransactionType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "contract")
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "action_party_id")
    private UserInfo actionParty;

    @Column(name = "action_party_fullname")
    private String actionPartyFullName;

    @Column(name = "action_party_address")
    private String actionPartyAddress;

    @Column(name = "quantity")
    private BigDecimal quantity;

    @Column(name = "pricePerOunce")
    private BigDecimal pricePerOunce;

    @Column(name = "totalCostOrProfit")
    private BigDecimal totalCostOrProfit;

    @Column(name = "transaction_type")
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Column(name = "createAt")
    private Date createdAt;

    @Column(name = "confirming_party")
    private String confirmingParty;

    @Column(name = "gold_unit")
    private GoldUnit goldUnit;

    @Column(name = "signature_action_party", nullable = true, columnDefinition = "TEXT")
    private String signatureActionParty;

    @Column(name = "signature_confirming_party", nullable = false, columnDefinition = "TEXT")
    private String signatureConfirmingParty;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", referencedColumnName = "id")
    private GoldTransaction transaction;

    @Enumerated(EnumType.STRING)
    @Column(name = "contract_status")
    private ContractStatus contractStatus;

    @Column(name = "contract_digital_signature", nullable = true, columnDefinition = "TEXT")
    private String contractDigitalSignature;

    @OneToMany(mappedBy = "contract", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ContractHistory> contractHistories;

    public Contract() {
    }

    public Contract(UserInfo actionParty, String actionPartyFullName, String actionPartyAddress,
                    BigDecimal quantity, BigDecimal pricePerOunce, BigDecimal totalCostOrProfit,
                    TransactionType transactionType, Date createdAt, String confirmingParty,
                    GoldUnit goldUnit, String signatureActionParty, String signatureConfirmingParty,
                    GoldTransaction transaction, ContractStatus contractStatus,
                    String contractDigitalSignature, List<ContractHistory> contractHistories) {
        this.actionParty = actionParty;
        this.actionPartyFullName = actionPartyFullName;
        this.actionPartyAddress = actionPartyAddress;
        this.quantity = quantity;
        this.pricePerOunce = pricePerOunce;
        this.totalCostOrProfit = totalCostOrProfit;
        this.transactionType = transactionType;
        this.createdAt = createdAt;
        this.confirmingParty = confirmingParty;
        this.goldUnit = goldUnit;
        this.signatureActionParty = signatureActionParty;
        this.signatureConfirmingParty = signatureConfirmingParty;
        this.transaction = transaction;
        this.contractStatus = contractStatus;
        this.contractDigitalSignature = contractDigitalSignature;
        this.contractHistories = contractHistories;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserInfo getActionParty() {
        return actionParty;
    }

    public void setActionParty(UserInfo actionParty) {
        this.actionParty = actionParty;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPricePerOunce() {
        return pricePerOunce;
    }

    public void setPricePerOunce(BigDecimal pricePerOunce) {
        this.pricePerOunce = pricePerOunce;
    }

    public BigDecimal getTotalCostOrProfit() {
        return totalCostOrProfit;
    }

    public void setTotalCostOrProfit(BigDecimal totalCostOrProfit) {
        this.totalCostOrProfit = totalCostOrProfit;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getConfirmingParty() {
        return confirmingParty;
    }

    public void setConfirmingParty(String confirmingParty) {
        this.confirmingParty = confirmingParty;
    }

    public GoldUnit getGoldUnit() {
        return goldUnit;
    }

    public void setGoldUnit(GoldUnit goldUnit) {
        this.goldUnit = goldUnit;
    }

    public String getSignatureActionParty() {
        return signatureActionParty;
    }

    public void setSignatureActionParty(String signatureActionParty) {
        this.signatureActionParty = signatureActionParty;
    }

    public String getSignatureConfirmingParty() {
        return signatureConfirmingParty;
    }

    public void setSignatureConfirmingParty(String signatureConfirmingParty) {
        this.signatureConfirmingParty = signatureConfirmingParty;
    }

    public String getActionPartyFullName() {
        return actionPartyFullName;
    }

    public void setActionPartyFullName(String fullName) {
        this.actionPartyFullName = fullName;
    }

    public String getActionPartyAddress() {
        return actionPartyAddress;
    }

    public void setActionPartyAddress(String address) {
        this.actionPartyAddress = address;
    }

    public GoldTransaction getTransaction() {
        return transaction;
    }

    public void setTransaction(GoldTransaction transaction) {
        this.transaction = transaction;
    }

    public ContractStatus getContractStatus() {
        return contractStatus;
    }

    public void setContractStatus(ContractStatus contractStatus) {
        this.contractStatus = contractStatus;
    }

    public String getContractDigitalSignature() {
        return contractDigitalSignature;
    }

    public void setContractDigitalSignature(String contractDigitalSignature) {
        this.contractDigitalSignature = contractDigitalSignature;
    }

    public List<ContractHistory> getContractHistories() {
        return contractHistories;
    }

    public void setContractHistories(List<ContractHistory> contractHistories) {
        this.contractHistories = contractHistories;
    }
}
