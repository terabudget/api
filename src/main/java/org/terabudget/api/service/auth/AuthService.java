package org.terabudget.api.service.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.terabudget.api.domain.BudgetUser;
import org.terabudget.api.domain.OAuthClient;
import org.terabudget.api.exception.OAuthCLientNotFoundException;
import org.terabudget.api.exception.UserNotFoundException;
import org.terabudget.api.factory.UserDetailsFactory;
import org.terabudget.api.model.auth.AuthResponse;
import org.terabudget.api.model.auth.LoginRequest;
import org.terabudget.api.model.auth.RefreshTokenValidationResult;
import org.terabudget.api.model.auth.TokenRefreshRequest;
import org.terabudget.api.repository.OAuthClientRepository;
import org.terabudget.api.repository.BudgetUserRepository;
import org.terabudget.api.util.JwtSupport;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Responsible for orchestrating the authentication process.
 */
@Service
public class AuthService {
    @Value("${org.terabudget.authentication.accessTokenExpirationMs}")
    private int accessTokenExpirationMs;
    @Autowired
    private JwtSupport jwtSupport;

    @Autowired
    private OAuthRefreshTokenService refreshTokenService;
    @Autowired
    private OAuthClientRepository oAuthClientRepository;

    @Autowired
    private BudgetUserRepository userRepository;
    @Autowired
    private UserDetailsFactory userDetailsFactory;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Do the Spring Security authentication.
     */
    public void authenticateWithSpring(UserDetails userDetails, HttpServletRequest httpServletRequest) {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities());
        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    /**
     * Authenticate for a login request.
     *
     * @param loginRequest
     * @return
     */
    public AuthResponse authenticate(LoginRequest loginRequest, HttpServletRequest httpServletRequest) {
        OAuthClient oAuthClient = oAuthClientRepository
                .findByClientIdAndSecret(loginRequest.getClientId(), loginRequest.getClientSecret())
                .orElseThrow(() -> new OAuthCLientNotFoundException(loginRequest.getClientId()));

        BudgetUser user = userRepository
                .findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new UserNotFoundException(loginRequest.getUsername()));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new UserNotFoundException(loginRequest.getUsername());
        }

        String refreshToken = refreshTokenService
                .createRefreshToken(user.getId(), oAuthClient.getClientId());

        String accessToken = jwtSupport.createAccessToken(user, accessTokenExpirationMs);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * Authenticate for an auth token.
     * 
     * @param authToken
     * @return
     */
    public Claims authenticate(String authToken, HttpServletRequest httpServletRequest) {
        Claims claims = jwtSupport.parseToken(authToken);
        UserDetails userDetails = userDetailsFactory.create(claims);
        authenticateWithSpring(userDetails, httpServletRequest);
        return claims;
    }

    /**
     * Authenticate for a refresh token.
     */
    public AuthResponse refresh(TokenRefreshRequest tokenRefreshRequest, HttpServletRequest httpServletRequest) {

        RefreshTokenValidationResult tokenValidationResult = refreshTokenService
                .validateRefreshRequest(tokenRefreshRequest);

        String accessToken = jwtSupport.createAccessToken(tokenValidationResult.getBudgetUser(),
                accessTokenExpirationMs);
        String newRefreshToken = refreshTokenService.createRefreshToken(tokenValidationResult);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .build();
    }
}
