package com.server.blockchainserver.repository.signature_repository;

import com.server.blockchainserver.platform.entity.OrganizationSignature;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationSignatureRepository extends JpaRepository<OrganizationSignature, Long> {
    OrganizationSignature findOrganizationSignatureById(Long id);
}
