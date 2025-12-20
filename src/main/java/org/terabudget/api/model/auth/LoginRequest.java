package org.terabudget.api.model.auth;

import jakarta.validation.constraints.NotBlank;
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
    @NotBlank
    private String clientId;
    @NotBlank
    private String clientSecret;
    @NotBlank
    @Size(min = 10, max = 255, message = "About Me must be between 10 and 255 characters")
    private String password;
    @NotBlank
    @Size(min = 1, max = 20, message = "Username must be between 10 and 20 characters")
    private String username;
}
