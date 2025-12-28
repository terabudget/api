package org.terabudget.api.it;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.terabudget.api.domain.BankAccount;
import org.terabudget.api.it.utils.IntegrationTestSupport;
import org.terabudget.api.model.auth.AuthResponse;
import org.terabudget.api.repository.BankAccountRepository;
import org.terabudget.api.repository.BankTransactionRepository;
import org.terabudget.api.repository.BudgetUserRepository;

import tools.jackson.core.type.TypeReference;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
public class BankAccountIT {

    @Value("${spring.liquibase.parameters.ui-client-id}")
    private String clientId;

    @Value("${spring.liquibase.parameters.ui-client-secret}")
    private String oAuthClientSecret;

    @Autowired
    private BankAccountRepository bankAccountRepository;
    @Autowired
    private BankTransactionRepository bankTransactionRepository;
    @Autowired
    private BudgetUserRepository budgetUserRepository;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        budgetUserRepository.deleteAll();
        bankTransactionRepository.deleteAll();
        bankAccountRepository.deleteAll();
    }

    @Test
    public void get_whenUnauthorised_thenForbidden() throws Exception {
        mockMvc.perform(get("/api/bank-accounts"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void get_whenNoAccounts_thenEmptyList() throws Exception {
        AuthResponse authResponse = IntegrationTestSupport.doSuccessfulSignup(mockMvc, clientId, oAuthClientSecret);

        String accountsJson = mockMvc.perform(get("/api/bank-accounts")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authResponse.getAccessToken()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<BankAccount> accounts = IntegrationTestSupport.getObjectMapper()
                .readValue(accountsJson, new TypeReference<List<BankAccount>>() {
                });

        assertTrue(accounts.isEmpty());
    }

    @Test
    public void get_whenAccountExists_thenReturned() throws Exception {
        AuthResponse authResponse = IntegrationTestSupport.doSuccessfulSignup(mockMvc, clientId, oAuthClientSecret);

        BankAccount account = Instancio.of(BankAccount.class)
                .ignore(field(BankAccount::getId))
                .create();

        bankAccountRepository.save(account);

        String accountsJson = mockMvc.perform(get("/api/bank-accounts")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authResponse.getAccessToken()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<BankAccount> accounts = IntegrationTestSupport.getObjectMapper()
                .readValue(accountsJson, new TypeReference<List<BankAccount>>() {
                });

        assertEquals(1, accounts.size());
        BankAccount actual = accounts.get(0);
        assertEquals(account.getName(), actual.getName());
    }
}
