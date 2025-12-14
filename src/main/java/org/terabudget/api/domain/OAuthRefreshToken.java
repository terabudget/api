package org.terabudget.api.domain;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

@Entity
@Table(name = "oauth_refresh_token")
@Data
@Builder
public class OAuthRefreshToken {

    @Id
    @GeneratedValue(generator = "uuid")
    private String id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private BudgetUser user;

    @OneToOne
    @JoinColumn(name = "oauth_client_id", referencedColumnName = "id", nullable = false)
    private OAuthClient oAuthClient;

    @Column(nullable = false, unique = true)
    private String payload;

    @Column(nullable = false)
    private Instant expirationDate;
}
