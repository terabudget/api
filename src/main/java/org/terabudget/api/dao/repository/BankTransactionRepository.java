package org.terabudget.api.dao.repository;

import java.util.List;

import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.terabudget.api.domain.BankTransaction;

@Repository
public interface BankTransactionRepository {
    @Query("MATCH (a:BankAccount)-[:ORIGINATED_FROM]->(t:BankTransaction) WHERE a.name = $name RETURN t")
    List<BankTransaction> findTransactionsByBankAccountName(@Param("name") String name);
}
