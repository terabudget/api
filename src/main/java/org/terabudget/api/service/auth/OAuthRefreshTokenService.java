package org.terabudget.api.service.auth;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.terabudget.api.domain.BudgetUser;
import org.terabudget.api.domain.OAuthClient;
import org.terabudget.api.domain.OAuthRefreshToken;
import org.terabudget.api.exception.OAuthCLientNotFoundException;
import org.terabudget.api.exception.TokenRefreshException;
import org.terabudget.api.exception.UserNotFoundException;
import org.terabudget.api.model.auth.RefreshTokenValidationResult;
import org.terabudget.api.model.auth.TokenRefreshRequest;
import org.terabudget.api.repository.BudgetUserRepository;
import org.terabudget.api.repository.OAuthClientRepository;
import org.terabudget.api.repository.OAuthRefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

/**
 * This service is responsible for managing refresh tokens.
 */
@Service
@Slf4j
public class OAuthRefreshTokenService {
    private static final String PART_SEPARATOR = ".";
    private static final String PART_SEPARATOR_REGEX = "[.]{1,}";
    private static final short EXPECTED_PARTS = 2;
    private static final short TOKEN_ID_POSITION = 0;
    private static final short TOKEN_PAYLOAD_POSITION = 1;
    private static final String REFRESH_TOKEN_FORMAT = "%s%s%s";

    @Value("${org.terabudget.authentication.refreshTokenExpirationMs}")
    private int refreshTokenExpirationMs;

    @Autowired
    private OAuthRefreshTokenRepository refreshTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OAuthClientRepository oAuthClientRepository;

    @Autowired
    private BudgetUserRepository userRepository;

    public OAuthRefreshToken getToken(String token, String clientId) {
        String[] parts = token.split(PART_SEPARATOR_REGEX);
        if (EXPECTED_PARTS != parts.length) {
            throw new TokenRefreshException(token,
                    clientId);
        }

        OAuthRefreshToken refreshToken = refreshTokenRepository
                .findByIdAndClientId(parts[TOKEN_ID_POSITION], clientId)
                .orElseThrow(() -> new TokenRefreshException(token, "Refresh token not found"));

        if (!passwordEncoder.matches(parts[TOKEN_PAYLOAD_POSITION], refreshToken.getPayload())) {
            throw new TokenRefreshException(token, clientId);
        }
        return refreshToken;
    }

    /**
     * Validate a refresh token.
     *
     * @param token
     * @param clientId
     * @return
     */
    public RefreshTokenValidationResult validateRefreshRequest(TokenRefreshRequest refreshRequest) {

        OAuthRefreshToken refreshToken = getToken(refreshRequest.getRefreshToken(), refreshRequest.getClientId());
        OAuthClient client = oAuthClientRepository
                .findByClientIdAndSecret(refreshRequest.getClientId(), refreshRequest.getClientSecret())
                .orElseThrow(() -> new OAuthCLientNotFoundException(refreshRequest.getClientId()));

        BudgetUser user = userRepository
                .findById(refreshToken.getBudgetUserId())
                .orElseThrow(() -> new UserNotFoundException(refreshToken.getBudgetUserId()));

        if (refreshToken.getExpirationDate().isBefore(Instant.now())) {
            throw new TokenRefreshException(refreshRequest.getRefreshToken(),
                    "Refresh token in the database has expired");
        }
        return RefreshTokenValidationResult.builder()
                .budgetUser(user)
                .client(client)
                .build();
    }

    /**
     * Deletes any existing refresh tokens for an existing refres token, and create
     * a new once.
     * 
     * @param refreshToken
     * @return
     */
    public String createRefreshToken(RefreshTokenValidationResult validationResult) {
        return createRefreshToken(validationResult.getBudgetUser().getId(), validationResult.getClient().getClientId());
    }

    /**
     * Deletes any existing refresh tokens for the user + client, and create a new
     * once.
     *
     * @param user
     * @param clientId
     * @return
     */
    @Transactional
    public String createRefreshToken(String userId, String clientId) {
        refreshTokenRepository.deleteByBudgetUserIdAndClientId(userId, clientId);

        String payload = UUID.randomUUID().toString();
        String encryptedPayload = passwordEncoder.encode(payload);

        OAuthRefreshToken refreshToken = OAuthRefreshToken
                .builder()
                .budgetUserId(userId)
                .clientId(clientId)
                .expirationDate(DateUtils.addMilliseconds(new Date(), refreshTokenExpirationMs).toInstant())
                .payload(encryptedPayload)
                .build();

        OAuthRefreshToken saved = refreshTokenRepository.save(refreshToken);

        return REFRESH_TOKEN_FORMAT.formatted(saved.getId(), PART_SEPARATOR, payload);
    }

    /**
     * Periodically delete all expired refresh tokens from the database.
     */
    @Transactional
    // @Scheduled(fixedDelay = 1000)
    public void deleteAllExpired() {
        int deletedCount = refreshTokenRepository.deleteAllWithExpiredDateBefore(Instant.now());
        log.info("Deleted {} expired refresh tokens", deletedCount);
    }
}