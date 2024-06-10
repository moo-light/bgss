package com.server.blockchainserver.platform.platform_services.services_interface;

import com.server.blockchainserver.platform.entity.OrganizationSignature;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface SignatureService {
    OrganizationSignature createOrganizationSignature(String organization, MultipartFile signature) throws IOException;
    List<OrganizationSignature> getOrganizationSignature();
}
