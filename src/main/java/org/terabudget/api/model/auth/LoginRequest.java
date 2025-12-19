package org.terabudget.api.model.auth;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @NotEmpty
    private String clientId;
    @NotEmpty
    private String clientSecret;
    @NotEmpty
    @Size(min = 10, max = 255, message = "About Me must be between 10 and 255 characters")
    private String password;
    @NotEmpty
    @Size(min = 1, max = 15, message = "Username must be between 10 and 15 characters")
    private String username;
}
