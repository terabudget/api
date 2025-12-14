package org.terabudget.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.terabudget.api.domain.BudgetUser;
import org.terabudget.api.exception.DuplicateUserException;
import org.terabudget.api.factory.UserFactory;
import org.terabudget.api.model.authentication.LoginRequest;
import org.terabudget.api.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserFactory userFactory;
    @Autowired
    private UserRepository userRepository;

    public BudgetUser createUser(LoginRequest loginRequest) {
        if (userRepository.existsByUsername(loginRequest.getUsername())) {
            throw new DuplicateUserException(loginRequest.getUsername());
        }

        BudgetUser user = userFactory.createUser(loginRequest);

        return userRepository.save(user);
    }
}
