package org.terabudget.api.dao.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.terabudget.api.domain.BankAccount;

@Repository
public interface BankAccountRepository {
    Optional<BankAccount> findOneByName(String name);

    List<BankAccount> findAllByName(String name);
}
