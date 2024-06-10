package com.server.blockchainserver.repository.shopping_repository;

import com.server.blockchainserver.models.shopping_model.CategoryPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryPostRepository extends JpaRepository<CategoryPost, Long> {
    List<CategoryPost> findAllByActive(boolean active);
}
