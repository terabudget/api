package org.terabudget.api.util;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.terabudget.api.domain.BudgetRole;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Support utilities for Spring Security.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SpringSecuritySupport {

    /**
     * Get a Spring Privilege for a BudgetRole.
     *
     * @param role
     * @return
     */
    public static SimpleGrantedAuthority getSpringPrivilegeForRole(BudgetRole role) {
        return getSpringPrivilegeForUrn(role.getUrn());
    }

    /**
     * Get a Spring Privilege for a URN.
     * 
     * @param urn
     * @return
     */
    public static SimpleGrantedAuthority getSpringPrivilegeForUrn(String urn) {
        String privilegeName = "ROLE_" + urn.toUpperCase().replace(":", "_").replace("-", "_");
        return new SimpleGrantedAuthority(privilegeName);
    }

}
