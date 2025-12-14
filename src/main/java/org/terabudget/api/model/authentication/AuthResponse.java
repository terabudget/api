package org.terabudget.api.model.authentication;

import org.terabudget.api.domain.BudgetUser;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    public String accessToken;
    public String clientId;
    public String refreshToken;

    @JsonIgnore
    public BudgetUser user;
}
