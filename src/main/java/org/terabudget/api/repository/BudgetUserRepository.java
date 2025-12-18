package org.terabudget.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.terabudget.api.domain.BudgetUser;

public interface BudgetUserRepository extends JpaRepository<BudgetUser, String> {
    Optional<BudgetUser> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

}
