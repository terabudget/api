package org.terabudget.api.factory;

import java.util.List;
import java.util.Map.Entry;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.terabudget.api.domain.BudgetUser;
import org.terabudget.api.model.authentication.spring.UserDetailsImpl;

import io.jsonwebtoken.Claims;

@Component
public class UserDetailsFactory {

    public SimpleGrantedAuthority authorityFromRoleName(Entry<String, Object> claim) {
        return new SimpleGrantedAuthority(claim.getValue().toString());
    }

    public UserDetails create(BudgetUser user) {
        return new UserDetailsImpl(
                user.getUsername(),
                "",
                List.of());
    }

    public UserDetails create(Claims claims) {

        return new UserDetailsImpl(
                claims.getSubject(),
                "",
                List.of());
    }
}
