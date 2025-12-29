package org.terabudget.api.it.utils;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.instancio.Instancio;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.terabudget.api.model.auth.AuthResponse;
import org.terabudget.api.model.auth.LoginRequest;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tools.jackson.databind.ObjectMapper;

/**
 * Support methods for integration tests.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class IntegrationTestSupport {

    public static final String USER_NAME = Instancio.gen().string().length(20).get();
    public static final String USER_PASSWORD = UUID.randomUUID().toString();

    @Getter
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static AuthResponse doSuccessfulSignup(MockMvc mockMvc, String clientId, String oAuthClientSecret) throws Exception {
        LoginRequest request = LoginRequest.builder()
                .clientId(clientId)
                .clientSecret(oAuthClientSecret)
                .username(USER_NAME)
                .password(USER_PASSWORD)
                .build();

        String requestJson = IntegrationTestSupport.getObjectMapper().writeValueAsString(request);
        String responseJSON = mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return IntegrationTestSupport.getObjectMapper()
                .readValue(responseJSON, AuthResponse.class);
    }

    /**
     * Perform a successful login and return the result.
     *
     * @param mockMvc
     * @param request
     * @return
     * @throws Exception
     */
    public static AuthResponse doSuccessfulLogin(MockMvc mockMvc, LoginRequest request) throws Exception {
        String responseJSON = doLogin(mockMvc, request)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(responseJSON, AuthResponse.class);
    }

    /**
     * Perform a login and return the ResultActions.
     *
     * @param mockMvc
     * @param request
     * @return
     * @throws Exception
     */
    public static ResultActions doLogin(MockMvc mockMvc, LoginRequest request) throws Exception {
        String requestJson = objectMapper.writeValueAsString(request);
        return mockMvc.perform(post("/api/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson));
    }
}
