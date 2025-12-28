package org.terabudget.api.it;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.instancio.Instancio;
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
import org.terabudget.api.domain.OAuthClient;
import org.terabudget.api.it.utils.IntegrationTestSupport;
import org.terabudget.api.model.auth.AuthResponse;
import org.terabudget.api.model.auth.LoginRequest;
import org.terabudget.api.repository.BudgetUserRepository;
import org.terabudget.api.repository.OAuthClientRepository;

/**
 * Integration tests for edge cases
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
public class EdgeCaseIT {

    @Value("${spring.liquibase.parameters.ui-client-id}")
    private String clientId;

    @Value("${spring.liquibase.parameters.ui-client-secret}")
    private String oAuthClientSecret;

    @Autowired
    private OAuthClientRepository oAuthClientRepository;

    @Autowired
    private BudgetUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void rootUrl_whenPost_thenForbidden() throws Exception {
        mockMvc.perform(post("/"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void wrongUrl_whenAuthenticated_thenNotFound() throws Exception {
        String username = Instancio.gen()
                .string()
                .minLength(1)
                .maxLength(20)
                .get();
        String password = UUID.randomUUID().toString();
        userRepository.deleteAll();
        BudgetUser user = BudgetUser.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .build();
        userRepository.save(user);

        AuthResponse authResponse = IntegrationTestSupport.doSuccessfulLogin(mockMvc, LoginRequest.builder()
                .clientId(clientId)
                .clientSecret(oAuthClientSecret)
                .username(username)
                .password(password)
                .build());

        mockMvc
                .perform(get("/api/auth/token-valid")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + authResponse.getAccessToken()))

                .andExpect(status().isNotFound());
    }

    @Test
    public void wrongUrl_whenNotAuthenticated_thenUnauthorised() throws Exception {
        mockMvc.perform(get("/asdasdasds"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void oAuthClient_whenAppStarted_thenCorrect() {
        OAuthClient client = oAuthClientRepository.findByClientId(clientId).get();
        assertEquals(oAuthClientSecret, client.getSecret());
        assertEquals(4, UUID.fromString(clientId).version());
    }
}
