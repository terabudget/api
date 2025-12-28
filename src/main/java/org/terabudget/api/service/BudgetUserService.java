package org.terabudget.api.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.terabudget.api.domain.BudgetUser;
import org.terabudget.api.exception.DuplicateUserException;
import org.terabudget.api.factory.UserFactory;
import org.terabudget.api.model.auth.LoginRequest;
import org.terabudget.api.repository.BudgetUserRepository;

@Service
public class BudgetUserService {
    @Autowired
    private UserFactory userFactory;
    @Autowired
    private BudgetUserRepository userRepository;

    public BudgetUser createUser(LoginRequest loginRequest) {
        Optional<BudgetUser> exitingUser = userRepository.findByUsername(loginRequest.getUsername());
        if (exitingUser.isPresent()) {
            throw new DuplicateUserException(loginRequest.getUsername());
        }

        BudgetUser user = userFactory.createUser(loginRequest);

        return userRepository.save(user);
    }

}
