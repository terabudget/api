package org.terabudget.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.terabudget.api.domain.BudgetRole;

public interface BudgetRoleRepository extends JpaRepository<BudgetRole, String> {

    Optional<BudgetRole> findByUrn(String urn);

}
