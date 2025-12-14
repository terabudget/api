package org.terabudget.api.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.terabudget.api.domain.BudgetUser;
import org.terabudget.api.model.authentication.LoginRequest;

@Component
public class UserFactory {
    @Autowired
    private PasswordEncoder encoder;

    public BudgetUser createUser(LoginRequest loginRequest) {
        return BudgetUser.builder()
                .username(loginRequest.getUsername())
                .password(encoder.encode(loginRequest.getPassword()))
                .build();
    }
}
