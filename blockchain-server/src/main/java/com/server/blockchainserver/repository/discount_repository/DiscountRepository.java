package com.server.blockchainserver.repository.discount_repository;

import com.server.blockchainserver.models.shopping_model.Discount;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {
    @Nonnull
    Optional<Discount> findById(@Nonnull Long id);

    Optional<Discount> findByCode(String code);
}
