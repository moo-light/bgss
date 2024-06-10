package com.server.blockchainserver.platform.entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "contract_history")
public class ContractHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id") // Sửa đổi này để tạo khóa ngoại
    private Contract contract;

    @Column(name = "contract_version")
    private String contractVersion;

    @Column(name = "contract_change_by")
    private String contractChangeBy;

    @Column(name = "contract_change_description")
    private String contractChangeDescription;

    @Column(name = "contract_change_date")
    private Date contractChangeDate;

    @Column(name = "contract_digital_signature")
    private String contractDigitalSignature;

    public ContractHistory() {}

    public ContractHistory(Contract contract, String contractVersion, String contractChangeBy,
                           String contractChangeDescription, Date contractChangeDate,
                           String contractDigitalSignature) {
        this.contract = contract;
        this.contractVersion = contractVersion;
        this.contractChangeBy = contractChangeBy;
        this.contractChangeDescription = contractChangeDescription;
        this.contractChangeDate = contractChangeDate;
        this.contractDigitalSignature = contractDigitalSignature;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public String getContractVersion() {
        return contractVersion;
    }

    public void setContractVersion(String contractVersion) {
        this.contractVersion = contractVersion;
    }

    public String getContractChangeBy() {
        return contractChangeBy;
    }

    public void setContractChangeBy(String contractChangeBy) {
        this.contractChangeBy = contractChangeBy;
    }

    public String getContractChangeDescription() {
        return contractChangeDescription;
    }

    public void setContractChangeDescription(String contractChangeDescription) {
        this.contractChangeDescription = contractChangeDescription;
    }

    public Date getContractChangeDate() {
        return contractChangeDate;
    }

    public void setContractChangeDate(Date contractChangeDate) {
        this.contractChangeDate = contractChangeDate;
    }

    public String getContractDigitalSignature() {
        return contractDigitalSignature;
    }

    public void setContractDigitalSignature(String contractDigitalSignature) {
        this.contractDigitalSignature = contractDigitalSignature;
    }
}
