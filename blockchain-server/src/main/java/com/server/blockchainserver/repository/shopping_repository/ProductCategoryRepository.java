package com.server.blockchainserver.repository.shopping_repository;

import com.server.blockchainserver.models.shopping_model.ProductCategory;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
    @Nonnull
    Optional<ProductCategory> findById(@Nonnull Long id);

    ProductCategory findProductCategoryByCategoryName(String name);

    List<ProductCategory> findAllByActive(boolean active);
}
