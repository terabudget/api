package org.terabudget.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import java.util.List;
import java.util.Optional;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;
import org.terabudget.api.domain.BankAccount;
import org.terabudget.api.dto.BankAccountCreateDTO;
import org.terabudget.api.service.BankAccountService;

@ExtendWith(MockitoExtension.class)
public class BankAccountControllerTest {

    @Mock
    private BankAccountService mockBankAccountService;
    @InjectMocks
    private BankAccountController bankAccountController;

    @Test
    public void getByIdSuccess() {
        BankAccount bankAccount = Instancio.create(BankAccount.class);
        doReturn(Optional.of(bankAccount)).when(mockBankAccountService).get(bankAccount.getId());
        assertEquals(bankAccount, bankAccountController.getById(bankAccount.getId()).getBody());
    }

    @Test
    public void getByIdNotFound() {
        String bankAccountId = "123";
        doReturn(Optional.empty()).when(mockBankAccountService).get(bankAccountId);
        assertEquals(HttpStatusCode.valueOf(HttpStatus.NOT_FOUND.value()),
                bankAccountController.getById(bankAccountId).getStatusCode());
    }

    @Test
    public void getAccountsSuccess() {
        BankAccount bankAccount = Instancio.create(BankAccount.class);
        List<BankAccount> accounts = List.of(bankAccount);
        doReturn(accounts).when(mockBankAccountService).getAccounts();
        assertEquals(accounts, bankAccountController.getAccounts());
    }

    @Test
    public void createBankAccountSuccess() {
        BankAccount bankAccount = Instancio.create(BankAccount.class);
        BankAccountCreateDTO createDTO = Instancio.create(BankAccountCreateDTO.class);
        doReturn(bankAccount).when(mockBankAccountService).createBankAccount(createDTO);
        assertEquals(bankAccount, bankAccountController.createAccount(createDTO).getBody());
    }

    @Test
    public void closeAccountSuccess() {
        BankAccount account = Instancio.create(BankAccount.class);
        doReturn(account).when(mockBankAccountService).closeBankAccount(account.getId());
        BankAccount returned = bankAccountController.closeAccount(account.getId()).getBody();
        assertEquals(account, returned);
    }

    @Test
    public void closeAccountIllegalArgumentExceptionThrowsResponseStatus() {
        String bankAccountId = "123";
        doThrow(IllegalArgumentException.class).when(mockBankAccountService).closeBankAccount(bankAccountId);
        assertThrows(ResponseStatusException.class, () -> bankAccountController.closeAccount(bankAccountId));
    }

    @Test
    public void createAccountSuccess() {
        BankAccount bankAccount = Instancio.create(BankAccount.class);
        BankAccountCreateDTO createDTO = Instancio.create(BankAccountCreateDTO.class);
        doReturn(bankAccount).when(mockBankAccountService).createBankAccount(createDTO);
        assertEquals(bankAccount, bankAccountController.createAccount(createDTO).getBody());
    }

    @Test
    public void createAccountThrowsException() {
        BankAccountCreateDTO createDTO = Instancio.create(BankAccountCreateDTO.class);
        doThrow(IllegalArgumentException.class).when(mockBankAccountService).createBankAccount(createDTO);
        assertThrows(ResponseStatusException.class, () -> bankAccountController.createAccount(createDTO));
    }

    @Test
    public void reopenAccountSuccess() {
        BankAccount account = Instancio.create(BankAccount.class);
        doReturn(account).when(mockBankAccountService).reopenBankAccount(account.getId());
        BankAccount returned = bankAccountController.reopenAccount(account.getId()).getBody();
        assertEquals(account, returned);
    }

    @Test
    public void reopenAccountIllegalArgumentExceptionThrowsResponseStatus() {
        String bankAccountId = "123";
        doThrow(IllegalArgumentException.class).when(mockBankAccountService).reopenBankAccount(bankAccountId);
        assertThrows(ResponseStatusException.class, () -> bankAccountController.reopenAccount(bankAccountId));
    }
}
