package org.terabudget.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.terabudget.api.domain.BankAccount;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, String> {
    Optional<BankAccount> findOneByName(String name);

    List<BankAccount> findAll();

    List<BankAccount> findAllByName(String name);
}
