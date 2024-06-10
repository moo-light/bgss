package com.server.blockchainserver.repository.shopping_repository;

import com.server.blockchainserver.models.shopping_model.CartItem;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @Nonnull
    Optional<CartItem> findById(@Nonnull Long id);

    List<CartItem> findAllByCartId(Long cartId);
}
