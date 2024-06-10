package com.server.blockchainserver.repository.user_repository;


import com.server.blockchainserver.models.user_model.User;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    // User findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    List<User> findByUsernameStartingWith(String prefix);

    @Nonnull
    Optional<User> findById(@Nonnull Long id);

    @Query("SELECT u FROM User u WHERE unaccent(lower(u.firstName)) LIKE unaccent(lower(:search)) or unaccent(lower(u.lastName)) LIKE unaccent(lower(:search))")
    List<User> findByFirstNameOrLastNameWithoutAccent(String search);


}
