package org.terabudget.api.it;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.terabudget.api.domain.OAuthClient;
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
    private MockMvc mockMvc;

    @Test
    public void rootUrl_whenPost_thenForbidden() throws Exception {
        mockMvc.perform(post("/"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void oAuthClient_whenAppStarted_thenCorrect() {
        OAuthClient client = oAuthClientRepository.findByClientId(clientId).get();
        assertEquals(oAuthClientSecret, client.getSecret());
        assertEquals(4, UUID.fromString(clientId).version());
    }
}
