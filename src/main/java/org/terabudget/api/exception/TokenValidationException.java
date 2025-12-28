package org.terabudget.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.NoArgsConstructor;

@ResponseStatus(HttpStatus.FORBIDDEN)
@NoArgsConstructor
public class TokenValidationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TokenValidationException(String token, String message) {
        super(String.format("Failed for [%s]: %s", token, message));
    }
}
