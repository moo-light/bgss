package com.server.blockchainserver.controllers.signature_controller;


import com.server.blockchainserver.platform.entity.OrganizationSignature;
import com.server.blockchainserver.platform.platform_services.services_interface.SignatureService;
import com.server.blockchainserver.repository.signature_repository.OrganizationSignatureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class OrganizationSignatureController {

    //Service
    @Autowired
    private SignatureService signatureService;

    @Autowired
    private OrganizationSignatureRepository organizationSignatureRepository;

    /**
     * Handles the HTTP PUT request to add a new organization signature.
     *
     * This method receives the organization's name and its signature file as parameters,
     * then it delegates the creation of the organization signature to the signature service.
     *
     * @param organization The name of the organization.
     * @param signature The signature file of the organization as a {@link MultipartFile}.
     * @return An {@link OrganizationSignature} object representing the newly added organization signature.
     * @throws IOException If an input or output exception occurred.
     */
    @PutMapping("/add-organization-signature")
    public OrganizationSignature addOrganizationSignature(@RequestParam("organization") String organization,
                                                          @RequestParam("signature") MultipartFile signature
                                                          ) throws IOException {
        return signatureService.createOrganizationSignature(organization, signature);
    }


    @GetMapping("/get-organization-signature")
    public List<OrganizationSignature> getOrganizationSignature() {
        return signatureService.getOrganizationSignature();
    }
    @GetMapping("/show-signature-organization")
    public  OrganizationSignature showSignatureOrganization() {
        long organizationId = 1;
        return organizationSignatureRepository.findOrganizationSignatureById(organizationId);

    }
}
