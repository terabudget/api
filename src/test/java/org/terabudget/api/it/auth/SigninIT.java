package org.terabudget.api.it.auth;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.terabudget.api.domain.BudgetRole;
import org.terabudget.api.domain.BudgetUser;
import org.terabudget.api.it.utils.IntegrationTestSupport;
import org.terabudget.api.model.auth.AuthResponse;
import org.terabudget.api.model.auth.BuiltInRoleUrn;
import org.terabudget.api.model.auth.LoginRequest;

import org.terabudget.api.repository.BudgetRoleRepository;
import org.terabudget.api.repository.BudgetUserRepository;
import org.terabudget.api.util.JwtSupport;

import io.jsonwebtoken.Claims;

/**
 * Integration tests for signing in.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
public class SigninIT {

    private static final String USER_NAME = Instancio.gen()
            .string()
            .minLength(1)
            .maxLength(20)
            .get();
    private static final String USER_PASSWORD = UUID.randomUUID().toString();

    private static final String ROLE_ASSIGNED_USER_NAME = Instancio.gen()
            .string()
            .minLength(1)
            .maxLength(20)
            .get();
    private static final String ROLE_ASSIGNED_USER_PASSWORD = UUID.randomUUID().toString();

    @Value("${spring.liquibase.parameters.ui-client-id}")
    private String clientId;

    @Value("${spring.liquibase.parameters.ui-client-secret}")
    private String oAuthClientSecret;

    @Autowired
    private BudgetUserRepository userRepository;

    @Autowired
    private BudgetRoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private BudgetUser noRolesUser;
    private BudgetUser rolesUser;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtSupport jwtSupport;

    @BeforeEach
    public void setup() throws Exception {
        BudgetRole role = roleRepository.findByUrn(BuiltInRoleUrn.ACCOUNT_CREATE_ANY.getUrn()).get();

        userRepository.deleteAll();
        noRolesUser = BudgetUser.builder()
                .username(USER_NAME)
                .password(passwordEncoder.encode(USER_PASSWORD))
                .build();

        rolesUser = BudgetUser.builder()
                .username(ROLE_ASSIGNED_USER_NAME)
                .password(passwordEncoder.encode(ROLE_ASSIGNED_USER_PASSWORD))
                .roles(Set.of(role))
                .build();

        userRepository.saveAll(List.of(rolesUser, noRolesUser));
    }

    @Test
    public void signin_whenRoles_thenClaimsAdded() throws Exception {
        AuthResponse authResponse = IntegrationTestSupport.doSuccessfulLogin(mockMvc, LoginRequest.builder()
                .clientId(clientId)
                .clientSecret(oAuthClientSecret)
                .username(ROLE_ASSIGNED_USER_NAME)
                .password(ROLE_ASSIGNED_USER_PASSWORD)
                .build());

        assertNotNull(authResponse.getRefreshToken());

        Claims claims = jwtSupport.parseToken(authResponse.getAccessToken());
        assertTrue(claims.containsKey(BuiltInRoleUrn.ACCOUNT_CREATE_ANY.getUrn()));
    }

    @Test
    public void signin_whenNoRoles_thenSuccess() throws Exception {

        AuthResponse authResponse = IntegrationTestSupport.doSuccessfulLogin(mockMvc, LoginRequest.builder()
                .clientId(clientId)
                .clientSecret(oAuthClientSecret)
                .username(USER_NAME)
                .password(USER_PASSWORD)
                .build());

        assertNotNull(authResponse.getAccessToken());
        assertNotNull(authResponse.getRefreshToken());
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
                .clientId(clientId)
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
                .clientId(clientId)
                .username("as")
                .password(USER_PASSWORD)
                .build())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void signin_whenBadPassword_thenUnauthorised() throws Exception {
        IntegrationTestSupport.doLogin(mockMvc, LoginRequest.builder()
                .clientSecret(oAuthClientSecret)
                .clientId(clientId)
                .username(USER_NAME)
                .password("1234567890")
                .build())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void signin_whenPasswordTooShort_thenUnauthorised() throws Exception {
        IntegrationTestSupport.doLogin(mockMvc, LoginRequest.builder()
                .clientSecret(oAuthClientSecret)
                .clientId(clientId)
                .username(USER_NAME)
                .password("as")
                .build())
                .andExpect(status().isBadRequest());
    }
}
