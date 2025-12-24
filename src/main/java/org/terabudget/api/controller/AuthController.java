package org.terabudget.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.terabudget.api.exception.OAuthCLientNotFoundException;
import org.terabudget.api.model.auth.AuthResponse;
import org.terabudget.api.model.auth.LoginRequest;
import org.terabudget.api.model.auth.TokenRefreshRequest;
import org.terabudget.api.repository.OAuthClientRepository;
import org.terabudget.api.service.BudgetUserService;
import org.terabudget.api.service.auth.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private OAuthClientRepository oAuthClientRepository;

    @Autowired
    private BudgetUserService userService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(HttpServletRequest httpServletRequest,
            @Valid @RequestBody LoginRequest signUpRequest) {

        oAuthClientRepository
                .findByClientIdAndSecret(signUpRequest.getClientId(), signUpRequest.getClientSecret())
                .orElseThrow(() -> new OAuthCLientNotFoundException(signUpRequest.getClientId()));

        userService.createUser(signUpRequest);

        return ResponseEntity.ok(authService.authenticate(signUpRequest, httpServletRequest));

    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signin(HttpServletRequest httpServletRequest,
            @Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.authenticate(loginRequest, httpServletRequest));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(HttpServletRequest httpServletRequest,
            @Valid @RequestBody TokenRefreshRequest tokenRefreshRequest) {
        return ResponseEntity.ok(authService.refresh(tokenRefreshRequest, httpServletRequest));
    }

    @GetMapping("/token-valid")
    public ResponseEntity<Object> getMethodName() {
        return ResponseEntity.noContent().build();
    }
}