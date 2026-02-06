package org.terabudget.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.terabudget.api.domain.BankAccount;
import org.terabudget.api.dto.BankAccountCreateDTO;
import org.terabudget.api.service.BankAccountService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/bank-accounts")
@Tag(name = "bank_accounts", description = "Bank account management endpoints")
public class BankAccountController {
    @Autowired
    private BankAccountService bankAccountService;

    /**
     * Get all bank accounts.
     * 
     * @return
     */
    @GetMapping
    @Operation(summary = "Get all bank accounts", description = "Returns a list of all bank accounts")
    public List<BankAccount> getAccounts() {
        return bankAccountService.getAccounts();
    }

    /**
     * Create a new bank account.
     * 
     * @param bankAccountCreateDTO
     * @return
     */
    @Operation(summary = "Create a new bank account", description = "Creates a new bank account with the provided details")
    @PostMapping
    public BankAccount createAccount(@RequestBody @Valid BankAccountCreateDTO bankAccountCreateDTO) {
        try {
            return bankAccountService.createBankAccount(bankAccountCreateDTO);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Delete a bank account by its ID.
     * 
     * @param bankAccountId
     */
    @DeleteMapping("/{bankAccountId}")
    @Operation(summary = "Delete a bank account", description = "Deletes a bank account by its ID")
    public void deleteAccount(String bankAccountId) {
        try {
            bankAccountService.deleteBankAccount(bankAccountId);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
