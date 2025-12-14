package org.terabudget.api.util;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.terabudget.api.domain.BudgetUser;
import org.terabudget.api.exception.TokenValidationException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;

@Component
public class JwtSupport {
    @Autowired
    private SecretKey secretKey;
    @Autowired
    private JwtParser jwtParser;

    /**
     * Shorthand method for creating a JWT with duration.
     * 
     * @param user
     * @param claims
     * @param durationMs
     * @return
     */
    public String createJwt(BudgetUser user, long durationMs) {
        return Jwts.builder()
                .expiration(Date.from(Instant.now().plusMillis(durationMs)))
                .issuedAt(new Date())
                .subject(user.getUsername())
                .signWith(secretKey)
                .compact();
    }

    /**
     * Shorthand method for creating a JWT with claims and duration.
     * 
     * @param user
     * @param claims
     * @param durationMs
     * @return
     */
    public String createJwt(BudgetUser user, Map<String, Object> claims, long durationMs) {
        return Jwts.builder()
                .claims(claims)
                .expiration(Date.from(Instant.now().plusMillis(durationMs)))
                .issuedAt(new Date())
                .subject(user.getUsername())
                .signWith(secretKey)
                .compact();
    }

    /**
     * Check if the token is expired.
     * 
     * @param token
     * @param claims
     */
    public void checkTokenExpiry(String token, Claims claims) {
        Date expiration = claims.getExpiration();
        if (expiration.before(new Date())) {
            throw new TokenValidationException(token,
                    "Token expired at " + expiration.toString());
        }
    }

    /**
     * Parse and validate a JWT token.
     * 
     * @param token
     * @return
     * @throws TokenValidationException
     */
    public Claims parseToken(String token) throws TokenValidationException {
        Claims claims;
        try {
            claims = jwtParser
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            throw new TokenValidationException(token, "Invalid JWT token: " + e.getMessage());
        }
        checkTokenExpiry(token, claims);
        return claims;
    }

    public String getSpringRoleForClaim(String claimName) {
        return "ROLE_" + claimName.toUpperCase().replace(":", "_").replace("-", "_");
    }
}
