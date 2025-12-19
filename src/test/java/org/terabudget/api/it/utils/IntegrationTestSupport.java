package org.terabudget.api.it.utils;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    @Getter
    private static final ObjectMapper objectMapper = new ObjectMapper();

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
        return mockMvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson));
    }
}
