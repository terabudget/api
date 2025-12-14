package org.terabudget.api.service.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.terabudget.api.domain.OAuthRefreshToken;
import org.terabudget.api.domain.BudgetUser;
import org.terabudget.api.domain.OAuthClient;
import org.terabudget.api.exception.OAuthCLientNotFoundException;
import org.terabudget.api.exception.UserNotFoundException;
import org.terabudget.api.factory.UserDetailsFactory;
import org.terabudget.api.model.authentication.AuthResponse;
import org.terabudget.api.model.authentication.LoginRequest;
import org.terabudget.api.model.authentication.TokenRefreshRequest;
import org.terabudget.api.repository.OAuthClientRepository;
import org.terabudget.api.repository.UserRepository;
import org.terabudget.api.util.JwtSupport;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Responsible for orchestrating the authentication process.
 */
@Service
public class AuthService {
    @Value("${org.terabudget.authentication.accessTokenExpirationMs}")
    private Long accessTokenExpirationMs;
    @Autowired
    private JwtSupport jwtSupport;

    @Autowired
    private OAuthRefreshTokenService refreshTokenService;
    @Autowired
    private OAuthClientRepository oAuthClientRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDetailsFactory userDetailsFactory;

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
                .findByIdAndSecret(loginRequest.getClientId(), loginRequest.getClientSecret())
                .orElseThrow(() -> new OAuthCLientNotFoundException(loginRequest.getClientId()));

        BudgetUser user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new UserNotFoundException(loginRequest.getUsername()));

        String refreshToken = refreshTokenService
                .createRefreshToken(user, oAuthClient);

        String accessToken = jwtSupport.createJwt(user, accessTokenExpirationMs);

        UserDetails userDetails = userDetailsFactory.create(user);
        authenticateWithSpring(userDetails, httpServletRequest);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(user)
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
        OAuthRefreshToken refreshToken = refreshTokenService
                .validateRefreshToken(tokenRefreshRequest.getRefreshToken());

        String accessToken = jwtSupport.createJwt(refreshToken.getUser(), accessTokenExpirationMs);
        String newRefreshToken = refreshTokenService.createRefreshToken(refreshToken);

        UserDetails userDetails = userDetailsFactory.create(refreshToken.getUser());
        authenticateWithSpring(userDetails, httpServletRequest);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .build();
    }
}
