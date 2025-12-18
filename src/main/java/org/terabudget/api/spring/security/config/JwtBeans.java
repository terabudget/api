package org.terabudget.api.spring.security.config;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Setter;

@Configuration
public class JwtBeans {

    @Value("${org.terabudget.authentication.jwtSecret}")
    @Setter
    private String jwtSecret;

    @Bean
    public SecretKey secretKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    @Bean
    public JwtParser jwtParser(SecretKey secretKey) {
        return Jwts
                .parser()
                .verifyWith(secretKey)
                .build();
    }
}
