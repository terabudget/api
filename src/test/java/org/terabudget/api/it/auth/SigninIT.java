package org.terabudget.api.it.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.terabudget.api.domain.BudgetUser;
import org.terabudget.api.it.utils.IntegrationTestSupport;
import org.terabudget.api.model.authentication.LoginRequest;

import jakarta.transaction.Transactional;

import org.terabudget.api.repository.BudgetUserRepository;

/**
 * Integration tests for authentication and authorisation.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
@Transactional
public class SigninIT {

    private static final String USER_NAME = UUID.randomUUID().toString();
    private static final String USER_PASSWORD = UUID.randomUUID().toString();

    @Value("${spring.liquibase.parameters.ui-client-id}")
    private String oAuthClientId;

    @Value("${spring.liquibase.parameters.ui-client-secret}")
    private String oAuthClientSecret;

    @Autowired
    private BudgetUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private BudgetUser budgetUser;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() throws Exception {
        userRepository.deleteAll();
        budgetUser = BudgetUser.builder()
                .username(USER_NAME)
                .password(passwordEncoder.encode(USER_PASSWORD))
                .build();
        userRepository.save(budgetUser);
    }

    @Test
    public void signin_success() throws Exception {

        IntegrationTestSupport.doLogin(mockMvc, LoginRequest.builder()
                .clientId(oAuthClientId)
                .clientSecret(oAuthClientSecret)
                .username(USER_NAME)
                .password(USER_PASSWORD)
                .build())
                .andExpect(status().isOk());
    }

    @Test
    public void signin_whenNoBody_thenUnauthorised() throws Exception {
        mockMvc.perform(post("/api/auth/signin"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void signin_whenWrongBodyType_thenUnauthorised() throws Exception {
        mockMvc.perform(post("/api/auth/signin")
                .content("test"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void signin_whenEmptyBody_thenUnauthorised() throws Exception {
        IntegrationTestSupport.doLogin(mockMvc, LoginRequest.builder().build())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void signin_whenBadClientSecret_thenUnauthorised() throws Exception {
        IntegrationTestSupport.doLogin(mockMvc, LoginRequest.builder()
                .clientSecret("as")
                .clientId(oAuthClientId)
                .username(USER_NAME)
                .password(USER_PASSWORD)
                .build())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void signin_whenBadClientId_thenUnauthorised() throws Exception {
        IntegrationTestSupport.doLogin(mockMvc, LoginRequest.builder()
                .clientSecret(oAuthClientSecret)
                .clientId("as")
                .username(USER_NAME)
                .password(USER_PASSWORD)
                .build())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void signin_whenBadUsername_thenUnauthorised() throws Exception {
        IntegrationTestSupport.doLogin(mockMvc, LoginRequest.builder()
                .clientSecret(oAuthClientSecret)
                .clientId(oAuthClientId)
                .username("as")
                .password(USER_PASSWORD)
                .build())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void signin_whenBadPassword_thenUnauthorised() throws Exception {
        IntegrationTestSupport.doLogin(mockMvc, LoginRequest.builder()
                .clientSecret(oAuthClientSecret)
                .clientId(oAuthClientId)
                .username(USER_NAME)
                .password("as")
                .build())
                .andExpect(status().isUnauthorized());
    }
}
