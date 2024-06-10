package com.server.blockchainserver.repository.discount_repository;

import com.server.blockchainserver.models.shopping_model.DiscountCodeOfUser;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiscountCodeOfUserRepository extends JpaRepository<DiscountCodeOfUser, Long> {
    @Nonnull
    Optional<DiscountCodeOfUser> findById(@Nonnull Long discountId);

    List<DiscountCodeOfUser> findAllByUserId(Long userId);

    DiscountCodeOfUser findByIdAndUserId(Long discountOfUserId, Long userId);

    List<DiscountCodeOfUser> findAllByDiscountId(Long discountId);

    DiscountCodeOfUser findByUserIdAndDiscountId(Long userId, Long discountId);
}
