package com.server.blockchainserver.repository.otp_verification;

import com.server.blockchainserver.gmail.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OtpVerificationRepository extends CrudRepository<OtpVerification, Long> {

    Optional<OtpVerification> findByReferenceIdAndAssociatedEntityAndExpiresAtAfter(
            String referenceId, String associatedEntity, LocalDateTime now);
    Optional<OtpVerification> findByReferenceIdAndAssociatedEntity(String referenceId, String associatedEntity);
}
