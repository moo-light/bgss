package com.server.blockchainserver.repository.shopping_repository;

import com.server.blockchainserver.models.shopping_model.TypeGold;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TypeGoldRepository extends JpaRepository<TypeGold, Long> {
    @Nonnull
    Optional<TypeGold> findById(@Nonnull Long id);

    List<TypeGold> findAllByActive(boolean active);
    TypeGold findByTypeName(String typeName);
}
