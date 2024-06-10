package com.server.blockchainserver.platform.server;


import com.server.blockchainserver.platform.entity.OrganizationSignature;
import com.server.blockchainserver.repository.signature_repository.OrganizationSignatureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Component
public class OrganizationSignatureManagement {

    @Autowired
    private OrganizationSignatureRepository organizationSignatureRepository;


    /**
     * Creates a new OrganizationSignature entity and saves it to the repository.
     * This method takes the name of an organization and a signature image file, converts the image to a Base64 encoded string,
     * and then creates a new OrganizationSignature entity with these details. The new entity is then saved to the repository.
     *
     * @param organization the name of the organization. It cannot be null or empty.
     * @param signature the signature image file as a MultipartFile. It cannot be null or empty.
     * @return the saved OrganizationSignature entity.
     * @throws IOException if an error occurs during file processing.
     * @throws IllegalArgumentException if the signature file is null or empty.
     */
    public OrganizationSignature createOrganizationSignature (String organization, MultipartFile signature) throws IOException {
        if (signature == null || signature.isEmpty()) {
            throw new IllegalArgumentException("Signature image must be provided.");
        }
        String base64Signature = Base64.getEncoder().encodeToString(signature.getBytes());
        OrganizationSignature organizationSignature = new OrganizationSignature();
        organizationSignature.setOrganization(organization);
        organizationSignature.setSignature(base64Signature);
        return organizationSignatureRepository.save(organizationSignature);
    }

    public List<OrganizationSignature> getOrganizationSignature(){
        return organizationSignatureRepository.findAll();
    }
}
