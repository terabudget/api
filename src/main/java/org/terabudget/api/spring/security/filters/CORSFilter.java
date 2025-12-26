package org.terabudget.api.spring.security.filters;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.terabudget.api.config.CorsConfig;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CORSFilter extends OncePerRequestFilter {
    @Autowired
    private CorsConfig config;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (HttpMethod.OPTIONS.name().equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setHeader("Access-Control-Allow-Methods", config.getAllowedMethodsString());
            response.setHeader("Access-Control-Allow-Headers",
                    config.getAllowedHeadersString());
            response.setHeader("Access-Control-Allow-Origin", config.getAllowedOriginsString());
        } else {
            filterChain.doFilter(request, response);
        }
    }

}