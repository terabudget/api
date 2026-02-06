package org.terabudget.api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.terabudget.api.domain.BankAccount;
import org.terabudget.api.dto.BankAccountCreateDTO;
import org.terabudget.api.repository.BankAccountRepository;

@ExtendWith(MockitoExtension.class)
class BankAccountServiceTest {

    @Mock
    private BankAccountRepository bankAccountRepository;

    @InjectMocks
    private BankAccountService bankAccountService;

    @Test
    void getAccounts_returnsAll() {
        List<BankAccount> accounts = List.of(BankAccount.builder().id("1").name("A").build());
        when(bankAccountRepository.findAll()).thenReturn(accounts);

        List<BankAccount> result = bankAccountService.getAccounts();

        assertEquals(accounts, result);
        verify(bankAccountRepository).findAll();
    }

    @Test
    void createBankAccount_whenExists_throws() {
        BankAccountCreateDTO dto = new BankAccountCreateDTO();
        dto.setName("Existing");

        when(bankAccountRepository.findOneByName("Existing"))
                .thenReturn(Optional.of(BankAccount.builder().id("1").name("Existing").build()));

        assertThrows(IllegalArgumentException.class, () -> bankAccountService.createBankAccount(dto));

        verify(bankAccountRepository, never()).save(any());
    }

    @Test
    void createBankAccount_savesNew() {
        BankAccountCreateDTO dto = new BankAccountCreateDTO();
        dto.setName("NewAccount");

        when(bankAccountRepository.findOneByName("NewAccount")).thenReturn(Optional.empty());
        when(bankAccountRepository.save(any(BankAccount.class)))
                .thenAnswer(invocation -> invocation.getArgument(0, BankAccount.class));

        BankAccount created = bankAccountService.createBankAccount(dto);

        assertEquals("NewAccount", created.getName());
        verify(bankAccountRepository).save(any(BankAccount.class));
    }

    @Test
    void deleteBankAccount_whenNotFound_throws() {
        when(bankAccountRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> bankAccountService.deleteBankAccount("1"));

        verify(bankAccountRepository, never()).deleteById(anyString());
    }

    @Test
    void deleteBankAccount_whenFound_deletes() {
        when(bankAccountRepository.findById("1"))
                .thenReturn(Optional.of(BankAccount.builder().id("1").name("A").build()));

        bankAccountService.deleteBankAccount("1");

        verify(bankAccountRepository).deleteById("1");
    }
}
