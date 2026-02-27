package org.terabudget.api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.terabudget.api.domain.BankAccount;
import org.terabudget.api.dto.BankAccountCreateDTO;
import org.terabudget.api.repository.BankAccountRepository;

@ExtendWith(MockitoExtension.class)
public class BankAccountServiceTest {

    @Mock
    private BankAccountRepository bankAccountRepository;

    @InjectMocks
    private BankAccountService bankAccountService;

    @Test
    void getByIdFound() {
        BankAccount existing = Instancio.create(BankAccount.class);
        when(bankAccountRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
        Optional<BankAccount> result = bankAccountService.get(existing.getId());
        assertEquals(existing, result.get());
    }

    @Test
    void getAccountsSuccess() {
        BankAccount existing = Instancio.create(BankAccount.class);
        List<BankAccount> accounts = List.of(existing);
        when(bankAccountRepository.findAll()).thenReturn(accounts);
        List<BankAccount> result = bankAccountService.getAccounts();
        assertEquals(accounts, result);
    }

    @Test
    void createBankAccountWhenExistsThrows() {
        BankAccountCreateDTO dto = Instancio.create(BankAccountCreateDTO.class);
        BankAccount existing = Instancio.create(BankAccount.class);
        when(bankAccountRepository.findOneByName(dto.getName()))
                .thenReturn(Optional.of(existing));
        assertThrows(IllegalArgumentException.class, () -> bankAccountService.createBankAccount(dto));
        verify(bankAccountRepository, never()).save(any());
    }

    @Test
    void createBankAccountSavesNew() {
        BankAccountCreateDTO dto = Instancio.create(BankAccountCreateDTO.class);
        when(bankAccountRepository.findOneByName(dto.getName())).thenReturn(Optional.empty());
        when(bankAccountRepository.save(any(BankAccount.class)))
                .thenAnswer(invocation -> invocation.getArgument(0, BankAccount.class));
        BankAccount created = bankAccountService.createBankAccount(dto);
        assertEquals(dto.getName(), created.getName());
    }

    @Test
    void deleteBankAccountWhenNotFoundThrows() {
        when(bankAccountRepository.findById("1")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> bankAccountService.deleteBankAccount("1"));
        verify(bankAccountRepository, never()).deleteById(anyString());
    }

    @Test
    void deleteBankAccountWhenFoundDeletes() {
        BankAccount account = Instancio.create(BankAccount.class);
        when(bankAccountRepository.findById(account.getId())).thenReturn(Optional.of(account));
        bankAccountService.deleteBankAccount(account.getId());

        verify(bankAccountRepository).deleteById(account.getId());
    }
}
