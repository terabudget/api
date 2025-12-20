package org.terabudget.api.it.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.terabudget.api.it.utils.IntegrationTestSupport;
import org.terabudget.api.model.auth.AuthResponse;
import org.terabudget.api.model.auth.LoginRequest;
import org.terabudget.api.repository.BudgetUserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
public class SignupIT {

    private static final String USER_NAME = Instancio.gen().string().length(20).get();
    private static final String USER_PASSWORD = UUID.randomUUID().toString();

    @Value("${spring.liquibase.parameters.ui-client-id}")
    private String clientId;

    @Value("${spring.liquibase.parameters.ui-client-secret}")
    private String oAuthClientSecret;

    @Autowired
    private BudgetUserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
    }

    private ResultActions doSignup(LoginRequest request) throws Exception {
        String requestJson = IntegrationTestSupport.getObjectMapper().writeValueAsString(request);
        return mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson));
    }

    @Test
    public void signup_success() throws UnsupportedEncodingException, Exception {
        AuthResponse authResponse = IntegrationTestSupport.doSuccessfulSignup(mockMvc, clientId, oAuthClientSecret);

        assertEquals(1, userRepository.findAll().size());
        assertNotNull(authResponse.getAccessToken());
        assertNotNull(authResponse.getRefreshToken());
    }

    @Test
    public void signup_whenDuplicateUser_thenReject() throws UnsupportedEncodingException, Exception {
        String responseJSON = doSignup(LoginRequest.builder()
                .clientId(clientId)
                .clientSecret(oAuthClientSecret)
                .username(USER_NAME)
                .password(USER_PASSWORD)
                .build())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        AuthResponse authResponse = IntegrationTestSupport.getObjectMapper()
                .readValue(responseJSON, AuthResponse.class);

        assertNotNull(authResponse.getAccessToken());
        assertNotNull(authResponse.getRefreshToken());

        doSignup(LoginRequest.builder()
                .clientId(clientId)
                .clientSecret(oAuthClientSecret)
                .username(USER_NAME)
                .password(USER_PASSWORD)
                .build())
                .andExpect(status().isConflict())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    public void signup_whenBadClientId_thenBadRequest() throws UnsupportedEncodingException, Exception {
        doSignup(LoginRequest.builder()
                .clientId("a")
                .clientSecret(oAuthClientSecret)
                .username(USER_NAME)
                .password(USER_PASSWORD)
                .build())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void signup_whenBadClientSecret_thenUnauthorised() throws UnsupportedEncodingException, Exception {
        doSignup(LoginRequest.builder()
                .clientId(clientId)
                .clientSecret("a")
                .username(USER_NAME)
                .password(USER_PASSWORD)
                .build())
                .andExpect(status().isUnauthorized());
    }
}
