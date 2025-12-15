package org.terabudget.api.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This represents a bank account from a financial instution.
 */
@Entity
@Table(name = "oauth_client")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OAuthClient {
    @Id
    @GeneratedValue(generator = "uuid")
    private String id;

    private String secret;
}
