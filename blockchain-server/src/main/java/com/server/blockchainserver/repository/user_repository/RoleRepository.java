package com.server.blockchainserver.repository.user_repository;

import com.server.blockchainserver.models.enums.ERole;
import com.server.blockchainserver.models.user_model.Role;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(ERole name);

    @Nonnull
    Optional<Role> findById(@Nonnull Long roleId);
}
