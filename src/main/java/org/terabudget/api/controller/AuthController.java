package org.terabudget.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.terabudget.api.model.authentication.AuthResponse;
import org.terabudget.api.model.authentication.LoginRequest;
import org.terabudget.api.model.authentication.TokenRefreshRequest;
import org.terabudget.api.service.UserService;
import org.terabudget.api.service.auth.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> registerUser(HttpServletRequest httpServletRequest,
            @Valid @RequestBody LoginRequest signUpRequest) {
        userService.createUser(signUpRequest);

        return ResponseEntity.ok(authService.authenticate(signUpRequest, httpServletRequest));
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> authenticateUser(HttpServletRequest httpServletRequest,
            @Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.authenticate(loginRequest, httpServletRequest));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(HttpServletRequest httpServletRequest,
            @Valid @RequestBody TokenRefreshRequest tokenRefreshRequest) {
        return ResponseEntity.ok(authService.refresh(tokenRefreshRequest, httpServletRequest));
    }

}