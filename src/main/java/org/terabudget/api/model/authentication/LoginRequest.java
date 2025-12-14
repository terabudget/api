package org.terabudget.api.model.authentication;

import lombok.Data;

@Data
public class LoginRequest {
    private String clientId;
    private String clientSecret;
    private String password;
    private String username;
}
