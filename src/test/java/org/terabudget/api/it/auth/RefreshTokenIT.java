package org.terabudget.api.it.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.terabudget.api.domain.BudgetRole;
import org.terabudget.api.domain.BudgetUser;
import org.terabudget.api.domain.OAuthRefreshToken;
import org.terabudget.api.it.utils.IntegrationTestSupport;
import org.terabudget.api.model.auth.AuthResponse;
import org.terabudget.api.model.auth.BuiltInRoleUrn;
import org.terabudget.api.model.auth.LoginRequest;
import org.terabudget.api.model.auth.TokenRefreshRequest;

import tools.jackson.databind.ObjectMapper;

import org.terabudget.api.repository.BudgetRoleRepository;
import org.terabudget.api.repository.BudgetUserRepository;
import org.terabudget.api.repository.OAuthRefreshTokenRepository;
import org.terabudget.api.service.auth.OAuthRefreshTokenService;

import jakarta.transaction.Transactional;

/**
 * Integration tests for signing in.
 */
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
public class RefreshTokenIT {
    private static final ObjectMapper objectMapper = new ObjectMapper();

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
    private BudgetRoleRepository roleRepository;

    @Autowired
    private OAuthRefreshTokenRepository oAuthRefreshTokenRepository;

    @Autowired
    private OAuthRefreshTokenService oAuthRefreshTokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private BudgetUser rolesUser;

    @Autowired
    private MockMvc mockMvc;

    private AuthResponse firstLoginResponse;

    @BeforeEach
    public void setup() throws Exception {
        BudgetRole role = roleRepository.findByUrn(BuiltInRoleUrn.ACCOUNT_CREATE_ANY.getUrn()).get();
        oAuthRefreshTokenRepository.deleteAll();
        userRepository.deleteAll();

        rolesUser = BudgetUser.builder()
                .username(USER_NAME)
                .password(passwordEncoder.encode(USER_PASSWORD))
                .enabled(true)
                .roles(Set.of(role))
                .build();

        userRepository.saveAll(List.of(rolesUser));

        firstLoginResponse = IntegrationTestSupport.doSuccessfulLogin(mockMvc, LoginRequest.builder()
                .clientId(clientId)
                .clientSecret(oAuthClientSecret)
                .username(USER_NAME)
                .password(USER_PASSWORD)
                .build());
    }

    @Test
    public void refreshToken_whenExpired_thenUnauthorised() throws Exception {

        OAuthRefreshToken refreshToken = oAuthRefreshTokenService.getToken(firstLoginResponse.getRefreshToken(),
                clientId);

        refreshToken.setExpirationDate(Instant.now().minus(5, ChronoUnit.DAYS));
        oAuthRefreshTokenRepository.save(refreshToken);

        // Allow the time to pass for the access token expiry to be different
        Thread.sleep(1000);

        TokenRefreshRequest request = TokenRefreshRequest.builder()
                .clientId(clientId)
                .clientSecret(oAuthClientSecret)
                .refreshToken(firstLoginResponse.getRefreshToken())
                .build();

        String requestJson = objectMapper.writeValueAsString(request);
        mockMvc.perform(post("/api/auth/refresh-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void refreshToken_whenCalled_thenSuccess() throws Exception {
        // Allow the time to pass for the access token expiry to be different
        Thread.sleep(1000);

        TokenRefreshRequest request = TokenRefreshRequest.builder()
                .clientId(clientId)
                .clientSecret(oAuthClientSecret)
                .refreshToken(firstLoginResponse.getRefreshToken())
                .build();

        String requestJson = objectMapper.writeValueAsString(request);
        String responseJson = mockMvc.perform(post("/api/auth/refresh-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        AuthResponse refreshResponse = objectMapper.readValue(responseJson, AuthResponse.class);

        assertNotNull(refreshResponse.getRefreshToken());
        assertNotNull(refreshResponse.getAccessToken());
        assertNotEquals(firstLoginResponse.getRefreshToken(), refreshResponse.getRefreshToken());
        assertNotEquals(firstLoginResponse.getAccessToken(), refreshResponse.getAccessToken());
        assertEquals(1, oAuthRefreshTokenRepository.count());
    }

    @Test
    public void refresh_whenNoBody_thenBadRequest() throws Exception {
        mockMvc.perform(post("/api/auth/refresh-token")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void refresh_whenEmptyBody_thenBadRequest() throws Exception {
        TokenRefreshRequest request = TokenRefreshRequest.builder()
                .build();

        String requestJson = objectMapper.writeValueAsString(request);
        mockMvc.perform(post("/api/auth/refresh-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void refresh_whenWongType_thenBadRequest() throws Exception {
        mockMvc.perform(post("/api/auth/refresh-token")
                .content("hello"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void refresh_whenBadClientSecret_thenUnauthorised() throws Exception {
        TokenRefreshRequest request = TokenRefreshRequest.builder()
                .clientSecret("as")
                .clientId(clientId)
                .refreshToken(firstLoginResponse.getRefreshToken())
                .build();

        String requestJson = objectMapper.writeValueAsString(request);
        mockMvc.perform(post("/api/auth/refresh-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void refresh_whenBadClientId_thenUnauthorised() throws Exception {
        TokenRefreshRequest request = TokenRefreshRequest.builder()
                .clientSecret(oAuthClientSecret)
                .clientId("as")
                .refreshToken(firstLoginResponse.getRefreshToken())
                .build();

        String requestJson = objectMapper.writeValueAsString(request);
        mockMvc.perform(post("/api/auth/refresh-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void refresh_whenBadRefreshToken_thenUnauthorised() throws Exception {
        TokenRefreshRequest request = TokenRefreshRequest.builder()
                .clientSecret(oAuthClientSecret)
                .clientId(clientId)
                .refreshToken("as")
                .build();

        String requestJson = objectMapper.writeValueAsString(request);
        mockMvc.perform(post("/api/auth/refresh-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isUnauthorized());
    }
}
