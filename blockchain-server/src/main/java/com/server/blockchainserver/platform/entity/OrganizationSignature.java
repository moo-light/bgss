package com.server.blockchainserver.platform.entity;


import jakarta.persistence.*;

@Entity
@Table(name = "organization_signature")
public class OrganizationSignature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String organization;

    @Lob
    @Column(name = "signature", nullable = false, columnDefinition = "TEXT")
    private String signature;

    public OrganizationSignature(String organization, String signature) {
        this.organization = organization;
        this.signature = signature;
    }

    public OrganizationSignature() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
