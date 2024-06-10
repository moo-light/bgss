package com.server.blockchainserver.repository.forum_repository;

import com.server.blockchainserver.models.shopping_model.Forum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForumRepository extends JpaRepository<Forum, Long> {
}
