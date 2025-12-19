package org.terabudget.api.model.auth;

import org.terabudget.api.domain.BudgetUser;
import org.terabudget.api.domain.OAuthClient;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RefreshTokenValidationResult {
    private BudgetUser budgetUser;
    private OAuthClient client;
}
