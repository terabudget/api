package org.terabudget.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.terabudget.api.domain.BankTransaction;
import org.terabudget.api.repository.BankTransactionRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/bank-accounts/{bankAccountId}/transactions")
@Tag(name = "bank_account_transactions", description = "Bank account transaction management endpoints")
public class BankTransactionController {
    @Autowired
    private BankTransactionRepository bankTransactionRepository;

    /**
     * Get all bank account transactions for a specific bank account.
     * 
     * @return
     */
    @GetMapping
    @Operation(summary = "Get all bank account transactions for a specific bank account", description = "Returns a list of all bank account transactions for a specific bank account")
    public List<BankTransaction> getTransactions(String bankAccountId) {
        return bankTransactionRepository.findTransactionsByOriginatingBankAccountId(bankAccountId);
    }
}
