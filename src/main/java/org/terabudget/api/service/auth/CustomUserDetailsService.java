package org.terabudget.api.service.auth;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.terabudget.api.domain.BudgetUser;
import org.terabudget.api.exception.UserNotFoundException;
import org.terabudget.api.model.auth.spring.UserDetailsImpl;
import org.terabudget.api.repository.BudgetUserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private BudgetUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        BudgetUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        return new UserDetailsImpl(user.getUsername(), "", List.of());
    }
}
