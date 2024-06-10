package com.server.blockchainserver.repository.shopping_repository;

import com.server.blockchainserver.models.shopping_model.Order;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Nonnull
    Optional<Order> findById(@Nonnull Long id);
    Optional<Order> findByQrCode(@Nonnull String qr_code);

    List<Order> findAllByOrderByIdDesc();

    List<Order> findAllByUserId(Long orderId);
}
