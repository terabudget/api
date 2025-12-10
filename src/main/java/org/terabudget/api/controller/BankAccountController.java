package org.terabudget.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.terabudget.api.domain.BankAccount;
import org.terabudget.api.service.BankAccountService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping(value = "/api/bank-accounts")
@Slf4j
public class BankAccountController {

    @Autowired
    private BankAccountService bankAccountService;

    @GetMapping
    public List<BankAccount> findAll() {
        log.info("Fetching all bank accounts");
        return bankAccountService.getAllBankAccounts();
    }

    @PostMapping
    public void create(@Valid @RequestBody BankAccount bankAccount) {
        log.info("Creating bank account: {}", bankAccount.getName());
        bankAccountService.createBankAccount(bankAccount);
    }

}
