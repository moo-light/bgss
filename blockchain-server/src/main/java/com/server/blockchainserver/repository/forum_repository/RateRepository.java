package com.server.blockchainserver.repository.forum_repository;

import com.server.blockchainserver.models.shopping_model.Rate;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RateRepository extends JpaRepository<Rate, Long> {
    @Nonnull
    Optional<Rate> findById(@Nonnull Long id);

    List<Rate> findRatesByUserIdAndPostId(Long userId, Long postId);

    List<Rate> findRatesByPostId(Long postId);

    List<Rate> findRatesByUserId(Long userId);
}
