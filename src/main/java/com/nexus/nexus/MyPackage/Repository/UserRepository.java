package com.nexus.nexus.MyPackage.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nexus.nexus.MyPackage.Entities.UserModal;

public interface UserRepository extends JpaRepository<UserModal, Long> {

    Optional<UserModal> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<UserModal> findByEmail(String email);

    Optional<UserModal> findByUserId(String userId);

    @Query("SELECT u FROM UserModal u LEFT JOIN FETCH u.followers LEFT JOIN FETCH u.following WHERE u.id = :id")
    Optional<UserModal> findByIdWithFollows(@Param("id") Long id);
}
