package org.terabudget.api.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.terabudget.api.domain.BankAccount;
import org.terabudget.api.dto.BankAccountCreateDTO;
import org.terabudget.api.repository.BankAccountRepository;

/**
 * Service for managing bank accounts.
 */
@Service
public class BankAccountService {
    @Autowired
    private BankAccountRepository bankAccountRepository;

    public Optional<BankAccount> get(String id) {
        return bankAccountRepository.findById(id);
    }

    /**
     * Get all bank accounts.
     * 
     * @return
     */
    public List<BankAccount> getAccounts() {
        return bankAccountRepository.findAll();
    }

    /**
     * Create a new bank account.
     * 
     * @param bankAccountCreateDTO
     * @return
     */
    public BankAccount createBankAccount(final BankAccountCreateDTO bankAccountCreateDTO) {
        bankAccountRepository.findOneByName(bankAccountCreateDTO.getName())
                .ifPresent(bankAccount -> {
                    throw new IllegalArgumentException(
                            "Bank account already exists with name: " + bankAccountCreateDTO.getName());
                });

        BankAccount bankAccount = BankAccount.builder()
                .name(bankAccountCreateDTO.getName())
                .isOnBudget(bankAccountCreateDTO.isOnBudget())
                .build();
        return bankAccountRepository.save(bankAccount);
    }

    /**
     * Close a bank account by its ID.
     * 
     * @param bankAccountId
     */
    public BankAccount closeBankAccount(String bankAccountId) {
        BankAccount account = bankAccountRepository.findById(bankAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Bank account not found with ID: " + bankAccountId));

        if (account.isClosed()) {
            return account;
        }

        account.setClosed(true);
        return bankAccountRepository.save(account);
    }

    /**
     * Reopen a bank account by its ID.
     * 
     * @param bankAccountId
     */
    public BankAccount reopenBankAccount(String bankAccountId) {
        BankAccount account = bankAccountRepository.findById(bankAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Bank account not found with ID: " + bankAccountId));

        if (!account.isClosed()) {
            return account;
        }

        account.setClosed(false);
        return bankAccountRepository.save(account);
    }
}
