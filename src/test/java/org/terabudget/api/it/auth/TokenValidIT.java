package org.terabudget.api.it.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.terabudget.api.domain.BudgetUser;
import org.terabudget.api.it.utils.IntegrationTestSupport;
import org.terabudget.api.model.auth.AuthResponse;
import org.terabudget.api.model.auth.LoginRequest;
import org.terabudget.api.repository.BudgetUserRepository;

import jakarta.transaction.Transactional;

/**
 * Integration tests to check that all the roles referred to in BuiltInRole
 * exist in the database and have the built in flag set.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
@Transactional
public class TokenValidIT {
    private static final String USER_NAME = Instancio.gen()
            .string()
            .minLength(1)
            .maxLength(20)
            .get();
    private static final String USER_PASSWORD = UUID.randomUUID().toString();

    @Value("${spring.liquibase.parameters.ui-client-id}")
    private String clientId;

    @Value("${spring.liquibase.parameters.ui-client-secret}")
    private String oAuthClientSecret;

    @Autowired
    private BudgetUserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setup() throws Exception {
        userRepository.deleteAll();
        BudgetUser user = BudgetUser.builder()
                .username(USER_NAME)
                .password(passwordEncoder.encode(USER_PASSWORD))
                .build();
        userRepository.save(user);
    }

    @Test
    public void tokenValidation_success() throws Exception {

        AuthResponse authResponse = IntegrationTestSupport.doSuccessfulLogin(mockMvc, LoginRequest.builder()
                .clientId(clientId)
                .clientSecret(oAuthClientSecret)
                .username(USER_NAME)
                .password(USER_PASSWORD)
                .build());

        mockMvc
                .perform(get("/api/auth/token-valid")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + authResponse.getAccessToken()))

                .andExpect(status().isNoContent());
    }

    @Test
    public void tokenValidation_whenWrongToken_thenUnauthorised() throws Exception {
        mockMvc
                .perform(get("/api/auth/token-valid")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer asdasd"))

                .andExpect(status().isUnauthorized());
    }
}
