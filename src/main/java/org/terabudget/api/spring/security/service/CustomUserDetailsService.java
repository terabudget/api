package org.terabudget.api.spring.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.terabudget.api.domain.BudgetUser;
import org.terabudget.api.exception.UserNotFoundException;
import org.terabudget.api.factory.UserDetailsFactory;
import org.terabudget.api.repository.BudgetUserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserDetailsFactory userDetailsFactory;
    @Autowired
    private BudgetUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        BudgetUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        return userDetailsFactory.create(user);
    }
}
