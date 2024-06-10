package com.server.blockchainserver.repository.shopping_repository;

import com.server.blockchainserver.models.shopping_model.Cart;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    @Nonnull
    Optional<Cart> findById(@Nonnull Long id);

    Optional<Cart> findCartByUserId(Long id);

}
