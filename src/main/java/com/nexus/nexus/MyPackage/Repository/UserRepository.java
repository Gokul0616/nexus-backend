package com.nexus.nexus.MyPackage.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nexus.nexus.MyPackage.Entities.UserModal;

public interface UserRepository extends JpaRepository<UserModal, Long> {

    Optional<UserModal> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<UserModal> findByEmail(String email);
}
