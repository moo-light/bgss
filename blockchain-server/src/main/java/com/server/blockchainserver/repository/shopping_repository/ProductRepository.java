package com.server.blockchainserver.repository.shopping_repository;

import com.server.blockchainserver.models.enums.EGoldOptionType;
import com.server.blockchainserver.models.shopping_model.Product;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Nonnull
    Optional<Product> findById(@Nonnull Long id);

    List<Product> findProductByCategoryId(Long categoryId);
    List<Product> findAllByActive(boolean active);
    List<Product> findAllByTypeGoldId(Long typeGoldId);
    List<Product> findAllByTypeGoldOption(EGoldOptionType goldOptionType);
}
