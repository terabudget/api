package org.terabudget.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.terabudget.api.domain.BankAccount;
import org.terabudget.api.service.BankAccountService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping(value = "/api/bank-accounts")
public class BankAccountController {

    @Autowired
    private BankAccountService bankAccountService;

    @GetMapping
    public List<BankAccount> findAll() {
        return bankAccountService.getAllBankAccounts();
    }

    @PostMapping
    public void create(@RequestBody BankAccount bankAccount) {
        bankAccountService.createBankAccount(bankAccount);
    }

}
