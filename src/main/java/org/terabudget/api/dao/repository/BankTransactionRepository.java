package org.terabudget.api.dao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.terabudget.api.domain.BankTransaction;

@Repository
public interface BankTransactionRepository extends JpaRepository<BankTransaction, String> {
    List<BankTransaction> findTransactionsByOriginatingBankAccountName(String name);
}
