package com.server.blockchainserver.repository.shopping_repository;

import com.server.blockchainserver.models.shopping_model.ReviewProduct;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewProductRepository extends JpaRepository<ReviewProduct, Long> {
    @Nonnull
    Optional<ReviewProduct> findById(@Nonnull Long id);

    List<ReviewProduct> findAllByProductId(Long productId);

    ReviewProduct findByUserIdAndProductId(Long userId, Long productId);
    ReviewProduct findByIdAndUserId(Long rvId, Long userId);
}
