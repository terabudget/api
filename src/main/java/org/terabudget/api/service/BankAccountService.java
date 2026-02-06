package org.terabudget.api.service;

import java.util.List;

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

        BankAccount bankAccount = new BankAccount();
        bankAccount.setName(bankAccountCreateDTO.getName());
        return bankAccountRepository.save(bankAccount);
    }

    /**
     * Delete a bank account by its ID.
     * 
     * @param bankAccountId
     */
    public void deleteBankAccount(String bankAccountId) {
        bankAccountRepository.findById(bankAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Bank account not found with ID: " + bankAccountId));
        bankAccountRepository.deleteById(bankAccountId);
    }
}
