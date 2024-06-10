package com.server.blockchainserver.repository.forum_repository;

import com.server.blockchainserver.models.shopping_model.Post;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Nonnull
    Optional<Post> findById(@Nonnull Long id);

    List<Post> findAllByCategoryPostId(Long categoryPostId);
}
