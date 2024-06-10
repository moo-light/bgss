package com.server.blockchainserver.platform.platform_services.signature_service;

import com.server.blockchainserver.platform.entity.OrganizationSignature;
import com.server.blockchainserver.platform.platform_services.services_interface.SignatureService;
import com.server.blockchainserver.platform.server.OrganizationSignatureManagement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class SignatureServiceimpl implements SignatureService {


    @Autowired
    private OrganizationSignatureManagement organizationSignatureManagement;

    @Override
    public OrganizationSignature createOrganizationSignature(String organization, MultipartFile signature) throws IOException {
        return organizationSignatureManagement.createOrganizationSignature(organization, signature);
    }

    @Override
    public List<OrganizationSignature> getOrganizationSignature() {
        return organizationSignatureManagement.getOrganizationSignature();
    }
}
