package com.ailab.user.repository;

import com.ailab.user.domain.Role;
import com.ailab.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, String id);

    boolean existsByUsernameIgnoreCaseAndIdNot(String username, String id);

    long countByRole(Role role);

    long countByStatus(String status);
}
