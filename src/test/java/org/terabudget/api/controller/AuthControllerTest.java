package org.terabudget.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.Optional;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.terabudget.api.domain.OAuthClient;
import org.terabudget.api.exception.OAuthCLientNotFoundException;
import org.terabudget.api.model.auth.AuthResponse;
import org.terabudget.api.model.auth.LoginRequest;
import org.terabudget.api.repository.OAuthClientRepository;
import org.terabudget.api.service.BudgetUserService;
import org.terabudget.api.service.auth.AuthService;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private AuthService mockAuthService;
    @Mock
    private OAuthClientRepository mockOAuthClientRepository;
    @Mock
    private BudgetUserService mockBudgetUserService;

    @InjectMocks
    private AuthController authController;

    @Test
    public void signUp_whenNoClient_thenThrow() {
        LoginRequest req = Instancio.create(LoginRequest.class);
        assertThrows(OAuthCLientNotFoundException.class, () -> authController.signup(req));
        verifyNoInteractions(mockBudgetUserService);
    }

    @Test
    public void signUp_success() {
        LoginRequest req = Instancio.create(LoginRequest.class);
        AuthResponse res = Instancio.create(AuthResponse.class);

        OAuthClient oAuthClient = Instancio.create(OAuthClient.class);
        Optional<OAuthClient> foundClient = Optional.of(oAuthClient);
        doReturn(foundClient).when(mockOAuthClientRepository)
                .findByClientIdAndSecret(req.getClientId(), req.getClientSecret());

        doReturn(res).when(mockAuthService).authenticate(req);

        ResponseEntity<AuthResponse> entity = authController.signup(req);
        assertEquals(res, entity.getBody());
        assertEquals(HttpStatus.OK, entity.getStatusCode());
    }
}
