package org.terabudget.api.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.terabudget.api.domain.BudgetUser;
import org.terabudget.api.model.auth.LoginRequest;

@ExtendWith(MockitoExtension.class)
public class UserFactoryTest {
    @Mock
    private PasswordEncoder mockPasswordEncoder;

    @InjectMocks
    public UserFactory userFactory;

    @Test
    public void createUser_success() {
        LoginRequest req = Instancio.create(LoginRequest.class);
        doReturn("tested").when(mockPasswordEncoder).encode(req.getPassword());

        BudgetUser user = userFactory.createUser(req);
        assertEquals(req.getUsername(), user.getUsername());
        assertEquals("tested", user.getPassword());
    }

}
