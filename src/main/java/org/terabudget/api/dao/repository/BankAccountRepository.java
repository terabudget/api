package org.terabudget.api.dao.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;
import org.terabudget.api.domain.BankAccount;

@Repository
public interface BankAccountRepository extends Neo4jRepository<BankAccount, String> {
    Optional<BankAccount> findOneByName(String name);

    List<BankAccount> findAll();

    List<BankAccount> findAllByName(String name);
}
