package org.terabudget.api.controller.advice;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.terabudget.api.exception.OAuthCLientNotFoundException;
import org.terabudget.api.exception.TokenRefreshException;
import org.terabudget.api.exception.TokenValidationException;
import org.terabudget.api.exception.UserNotFoundException;
import org.terabudget.api.model.ApiError;

import java.net.http.HttpHeaders;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestControllerAdvice
public class AuthControllerAdvice extends ResponseEntityExceptionHandler {

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
    public ResponseEntity<Object> handleTokenRefreshException(TokenRefreshException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {

        List<String> errors = new ArrayList<String>();
        errors.add(ex.getMessage());

        ApiError apiError = new ApiError(HttpStatus.FORBIDDEN, ex.getLocalizedMessage(), errors);

        return handleExceptionInternal(
                ex, apiError, null, HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(TokenValidationException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<Object> handleTokenValidationException(TokenValidationException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {

        List<String> errors = new ArrayList<String>();
        errors.add(ex.getMessage());

        ApiError apiError = new ApiError(HttpStatus.FORBIDDEN, ex.getLocalizedMessage(), errors);

        return handleExceptionInternal(
                ex, apiError, null, HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {

        List<String> errors = new ArrayList<String>();
        errors.add(ex.getMessage());

        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED, ex.getLocalizedMessage(), errors);

        return handleExceptionInternal(
                ex, apiError, null, HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler(OAuthCLientNotFoundException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<Object> OAuthCLientNotFoundException(OAuthCLientNotFoundException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {

        List<String> errors = new ArrayList<String>();
        errors.add(ex.getMessage());

        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED, ex.getLocalizedMessage(), errors);

        return handleExceptionInternal(
                ex, apiError, null, HttpStatus.UNAUTHORIZED, request);
    }
}