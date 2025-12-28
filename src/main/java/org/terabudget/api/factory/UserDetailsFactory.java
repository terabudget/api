package org.terabudget.api.factory;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.terabudget.api.domain.BudgetUser;
import org.terabudget.api.model.auth.spring.UserDetailsImpl;
import org.terabudget.api.util.SpringSecuritySupport;

import io.jsonwebtoken.Claims;

@Component
public class UserDetailsFactory {

    public UserDetails create(BudgetUser user) {
        List<SimpleGrantedAuthority> grantedAuthorities = user.getRoles().stream()
                .map(SpringSecuritySupport::getSpringPrivilegeForRole)
                .collect(Collectors.toList());

        return new UserDetailsImpl(user.getUsername(), "", grantedAuthorities);
    }

    public UserDetails create(Claims claims) {
        return new UserDetailsImpl(
                claims.getSubject(),
                "",
                List.of());
    }
}
