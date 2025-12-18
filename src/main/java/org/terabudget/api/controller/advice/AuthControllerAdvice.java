package org.terabudget.api.controller.advice;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.terabudget.api.exception.OAuthCLientNotFoundException;
import org.terabudget.api.exception.TokenRefreshException;
import org.terabudget.api.exception.TokenValidationException;
import org.terabudget.api.exception.UserNotFoundException;
import org.terabudget.api.factory.ResponseEntityFactory;
import org.terabudget.api.model.api.ApiError;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;

@RestControllerAdvice
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class AuthControllerAdvice {
    @Autowired
    private ResponseEntityFactory responseEntityFactory;

    /**
     * Handle token exceptions
     *
     * @param ex
     * @param headers
     * @param status
     * @param request
     * @return
     */
    @ExceptionHandler(TokenRefreshException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ApiError> handleTokenRefreshException(TokenRefreshException ex) {
        return responseEntityFactory.errorResponse(ex, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(InsufficientAuthenticationException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ApiError> handleInsufficientAuthenticationException(InsufficientAuthenticationException ex) {
        return responseEntityFactory.errorResponse(ex, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(TokenValidationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ApiError> handleTokenValidationException(TokenValidationException ex) {
        return responseEntityFactory.errorResponse(ex, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ApiError> handleUserNotFoundException(UserNotFoundException ex) {
        return responseEntityFactory.errorResponse(ex, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(OAuthCLientNotFoundException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ApiError> OAuthCLientNotFoundException(OAuthCLientNotFoundException ex) {
        return responseEntityFactory.errorResponse(ex, HttpStatus.UNAUTHORIZED);
    }
}