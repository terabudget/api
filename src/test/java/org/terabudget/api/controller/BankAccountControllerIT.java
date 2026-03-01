package org.terabudget.api.controller;

import static org.instancio.Select.field;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.instancio.Instancio;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.terabudget.api.domain.BankAccount;
import org.terabudget.api.dto.BankAccountCreateDTO;
import org.terabudget.api.repository.BankAccountRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BankAccountControllerIT {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BankAccountRepository bankAccountRepository;

    @BeforeEach
    public void setUp() {
        bankAccountRepository.deleteAll();
    }

    @Test
    public void createAccountSuccess() throws Exception {
        BankAccountCreateDTO createDTO = Instancio.create(BankAccountCreateDTO.class);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(createDTO);

        mockMvc.perform(post("/api/bank-accounts")
                .content(json)
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andExpect(content().json("""
                        {
                            "name": "%s",
                            "onBudget": %s
                        }
                        """.formatted(createDTO.getName(), createDTO.isOnBudget())));
    }

    @Test
    public void createAccountDuplicateThrowsException() throws Exception {
        BankAccountCreateDTO createDTO = Instancio.create(BankAccountCreateDTO.class);
        ObjectMapper objectMapper = new ObjectMapper();
        BankAccount account = BankAccount.builder()
                .name(createDTO.getName())
                .build();
        bankAccountRepository.save(account);
        String json = objectMapper.writeValueAsString(createDTO);
        mockMvc.perform(post("/api/bank-accounts")
                .content(json)
                .contentType("application/json"))
                .andExpect(status().isConflict());
    }

    @Test
    public void getAccountReturnsNotfound() throws Exception {
        mockMvc.perform(get("/api/bank-accounts/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getAccountReturnsAccount() throws Exception {
        BankAccount account = Instancio.of(BankAccount.class)
                .ignore(field(BankAccount::getId))
                .set(field(BankAccount::isClosed), true)
                .create();
        bankAccountRepository.save(account);
        mockMvc.perform(get("/api/bank-accounts/" + account.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                            "name": "%s",
                            "onBudget": %s,
                            "closed": %s
                        }
                        """.formatted(account.getName(), account.isOnBudget(), account.isClosed())));
    }

    @Test
    public void getAccountsReturnsEmptyList() throws Exception {
        mockMvc.perform(get("/api/bank-accounts"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    public void getAccountsReturnsAccounts() throws Exception {
        BankAccount account = Instancio.of(BankAccount.class)
                .ignore(field(BankAccount::getId))
                .create();
        bankAccountRepository.save(account);

        mockMvc.perform(get("/api/bank-accounts"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [
                            {

                                    "name": "%s",
                                    "onBudget": %s
                            }
                        ]
                        """.formatted(account.getName(), account.isOnBudget())));
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    public void closeAccount(boolean isAlreadyClosed) throws Exception {
        BankAccount account = Instancio.of(BankAccount.class)
                .set(field(BankAccount::isClosed), isAlreadyClosed)
                .ignore(field(BankAccount::getId))
                .create();
        BankAccount saved = bankAccountRepository.save(account);
        mockMvc.perform(post("/api/bank-accounts/" + saved.getId() + "/close"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                            "name": "%s",
                            "onBudget": %s,
                            "closed": true
                        }
                        """.formatted(saved.getName(), saved.isOnBudget())));
    }

    @Test
    public void closeAccountNotFound() throws Exception {
        mockMvc.perform(post("/api/bank-accounts/123456789/close"))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    public void reopenAccount(boolean isAlreadyClosed) throws Exception {
        BankAccount account = Instancio.of(BankAccount.class)
                .set(field(BankAccount::isClosed), isAlreadyClosed)
                .ignore(field(BankAccount::getId))
                .create();
        BankAccount saved = bankAccountRepository.save(account);
        mockMvc.perform(post("/api/bank-accounts/" + saved.getId() + "/reopen"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                            "name": "%s",
                            "onBudget": %s,
                            "closed": false
                        }
                        """.formatted(saved.getName(), saved.isOnBudget())));
    }

    @Test
    public void reopenAccountNotFound() throws Exception {
        mockMvc.perform(post("/api/bank-accounts/123456789/reopen"))
                .andExpect(status().isNotFound());
    }
}