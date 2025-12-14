package org.terabudget.api.repository;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.terabudget.api.domain.OAuthRefreshToken;
import org.terabudget.api.domain.BudgetUser;

@Repository
public interface OAuthRefreshTokenRepository extends JpaRepository<OAuthRefreshToken, String> {

    Optional<OAuthRefreshToken> findByPayloadAndOAuthClientId(String payload, String oAuthClientId);

    Optional<OAuthRefreshToken> findByUserUsernameAndOAuthClientId(String username, String oAuthClientId);

    @Modifying
    int deleteByUserAndOAuthClientId(BudgetUser user, String oAuthClientId);

    @Modifying
    @Query("delete from OAuthRefreshToken r where r.expirationDate <= :expirationDate")
    int deleteAllWithExpiredDateBefore(Instant expirationDate);
}