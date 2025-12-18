package org.terabudget.api.service.auth;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.terabudget.api.domain.OAuthRefreshToken;
import org.terabudget.api.domain.BudgetUser;
import org.terabudget.api.domain.OAuthClient;
import org.terabudget.api.exception.TokenRefreshException;
import org.terabudget.api.repository.OAuthRefreshTokenRepository;
import org.terabudget.api.repository.BudgetUserRepository;
import org.terabudget.api.util.JwtSupport;

import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

/**
 * This service is responsible for managing refresh tokens.
 */
@Service
@Slf4j
public class OAuthRefreshTokenService {
    @Value("${org.terabudget.authentication.refreshTokenExpirationMs}")
    private Long refreshTokenDurationMs;

    @Autowired
    private JwtSupport jwtSupport;

    @Autowired
    private OAuthRefreshTokenRepository refreshTokenRepository;

    @Autowired
    private BudgetUserRepository userRepository;

    /**
     * Validate a refresh token.
     *
     * @param token
     * @param clientId
     * @return
     */
    public OAuthRefreshToken validateRefreshToken(String token) {
        Claims claims = jwtSupport.parseToken(token);

        String payload = claims.get(ClaimKeys.REFRESH_PAYLOAD_CLAIM_KEY.getKey(), String.class);
        String clientId = claims.get(ClaimKeys.APPLICATION_ID_CLAIM_KEY.getKey(), String.class);

        OAuthRefreshToken refreshToken = refreshTokenRepository
                .findByPayloadAndOAuthClientId(payload, clientId)
                .orElseThrow(() -> new TokenRefreshException(token, "Refresh token not found"));

        if (!refreshToken.getUser().isEnabled()) {
            log.info("User {} is disabled", refreshToken.getUser().getUsername());
            throw new TokenRefreshException(token, "User is disabled");
        }

        if (refreshToken.getExpirationDate().isBefore(Instant.now())) {
            throw new TokenRefreshException(token, "Refresh token in the database has expired");
        }
        return refreshToken;
    }

    /**
     * Create a refresh token from an existing refresh token.
     * 
     * @param refreshToken
     * @return
     */
    public String createRefreshToken(OAuthRefreshToken refreshToken) {
        return createRefreshToken(refreshToken.getUser(), refreshToken.getOAuthClient());
    }

    /**
     * Create a refresh token.
     *
     * @param user
     * @param clientId
     * @return
     */
    @Transactional
    public String createRefreshToken(BudgetUser user, OAuthClient oAuthClient) {
        refreshTokenRepository.deleteByUserAndOAuthClientId(user, oAuthClient.getId());

        String payload = UUID.randomUUID().toString();

        OAuthRefreshToken refreshToken = OAuthRefreshToken
                .builder()
                .user(user)
                .oAuthClient(oAuthClient)
                .expirationDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .payload(payload)
                .build();

        refreshTokenRepository.save(refreshToken);

        return jwtSupport.createJwt(user.getUsername(),
                Map.of(ClaimKeys.REFRESH_PAYLOAD_CLAIM_KEY.getKey(), payload,
                        ClaimKeys.APPLICATION_ID_CLAIM_KEY.getKey(), oAuthClient.getId()),
                refreshTokenDurationMs);
    }

    /**
     * Delete refresh tokens by user and application ID.
     *
     * @param username
     * @param clientId
     * @return
     */
    @Transactional
    public int deleteByUserAndApplication(String username, String clientId) {
        return refreshTokenRepository.deleteByUserAndOAuthClientId(userRepository.findByUsername(username).get(),
                clientId);
    }

    /**
     * Periodically delete all expired refresh tokens from the database.
     */
    @Transactional
    @Scheduled(fixedDelay = 1000)
    public void deleteAllExpired() {
        int deletedCount = refreshTokenRepository.deleteAllWithExpiredDateBefore(Instant.now());
        log.info("Deleted {} expired refresh tokens", deletedCount);
    }
}