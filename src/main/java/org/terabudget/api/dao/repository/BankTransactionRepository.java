package org.terabudget.api.dao.repository;

import java.util.List;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.terabudget.api.domain.BankTransaction;

@Repository
public interface BankTransactionRepository extends Neo4jRepository<BankTransaction, String> {
    @Query("MATCH (a:BankAccount)-[:ORIGINATED_FROM]->(t:BankTransaction) WHERE a.name = $name RETURN t")
    List<BankTransaction> findTransactionsByBankAccountName(@Param("name") String name);
}
