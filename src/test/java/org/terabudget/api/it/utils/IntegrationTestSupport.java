package org.terabudget.api.it.utils;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.terabudget.api.model.authentication.LoginRequest;

import tools.jackson.databind.ObjectMapper;

public class IntegrationTestSupport {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static ResultActions doLogin(MockMvc mockMvc, LoginRequest request) throws Exception {
        String requestJson = objectMapper.writeValueAsString(request);
        return mockMvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson));
    }
}
