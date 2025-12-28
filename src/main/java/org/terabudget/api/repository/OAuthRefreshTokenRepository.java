package org.terabudget.api.repository;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.terabudget.api.domain.OAuthRefreshToken;

@Repository
public interface OAuthRefreshTokenRepository extends JpaRepository<OAuthRefreshToken, String> {

    Optional<OAuthRefreshToken> findByIdAndClientId(String id, String clientId);

    Optional<OAuthRefreshToken> findByBudgetUserIdAndClientId(String username, String clientId);

    @Modifying
    int deleteByBudgetUserIdAndClientId(String budgetUserId, String clientId);

    @Modifying
    @Query("delete from OAuthRefreshToken r where r.expirationDate <= :expirationDate")
    int deleteAllWithExpiredDateBefore(Instant expirationDate);
}