package org.terabudget.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.terabudget.api.domain.BudgetCategory;

/**
 * Repository for BudgetCategory entities.
 */
@Repository
public interface BudgetCategoryRepository extends JpaRepository<BudgetCategory, String> {
    Optional<BudgetCategory> findOneByName(String name);

    List<BudgetCategory> findAll();

    List<BudgetCategory> findAllByName(String name);
}
