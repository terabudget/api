package org.terabudget.api.spring.security.filters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.terabudget.api.exception.TokenValidationException;
import org.terabudget.api.spring.security.service.CustomUserDetailsService;
import org.terabudget.api.util.JwtSupport;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.jbosslog.JBossLog;

@Component
@JBossLog
public class CustomAuthorisationFilter extends OncePerRequestFilter {

    @Autowired
    private CustomUserDetailsService userDetailsService;
    @Autowired
    private JwtSupport jwtSupport;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        try {
            // Exclude specific paths from the filter chain, e.g., login, token validation.
            if (request.getServletPath().equalsIgnoreCase("/api/auth/signin") ||
                    request.getServletPath().equalsIgnoreCase("/api/auth/signup")) {
                filterChain.doFilter(request, response);
            } else {
                String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
                // Check if the authorization header is present and starts with "Bearer ".
                if (authorization != null && authorization.startsWith("Bearer ")) {
                    // Extract the token.
                    String token = authorization.substring("Bearer ".length());
                    // Extract the username from the token.
                    String username = jwtSupport.parseToken(token).getSubject();
                    // Load user details using the username.
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    // Validate the token and check if the user is not already authenticated.
                    if (userDetails != null
                            && SecurityContextHolder.getContext().getAuthentication() == null) {
                        // Create a new authentication token and set it in the SecurityContextHolder.
                        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities() // Pass the authorities for authorization checks.
                        );
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                }
                // Continue the filter chain.
                filterChain.doFilter(request, response);
            }
        } catch (Exception ex) {
            log.error("Error occurred in CustomAuthorizationFilter. Cause: {}", ex);
            throw new TokenValidationException();
        }
    }
}