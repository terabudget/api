package org.terabudget.api.domain;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "oauth_refresh_token")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OAuthRefreshToken {

    @Id
    @GeneratedValue(generator = "uuid")
    private String id;

    private String budgetUserId;

    private String clientId;

    @Column(nullable = false, unique = true)
    private String payload;

    @Column(nullable = false)
    private Instant expirationDate;
}
