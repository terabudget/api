package org.terabudget.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.terabudget.api.domain.OAuthClient;

public interface OAuthClientRepository extends JpaRepository<OAuthClient, String> {
    Optional<OAuthClient> findByIdAndSecret(String id, String secret);

}
