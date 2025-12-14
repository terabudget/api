package org.terabudget.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.terabudget.api.domain.BankAccount;
import org.terabudget.api.repository.BankAccountRepository;

@Service
public class BankAccountService {
    @Autowired
    private BankAccountRepository bankAccountRepository;

    public List<BankAccount> getAllBankAccounts() {
        return bankAccountRepository.findAll();
    }

    public void createBankAccount(BankAccount bankAccount) {
        bankAccountRepository.save(bankAccount);
    }
}
