package org.terabudget.api.controller.advice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.terabudget.api.exception.DuplicateUserException;
import org.terabudget.api.factory.ResponseEntityFactory;
import org.terabudget.api.model.api.ApiError;

@RestControllerAdvice
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class UserControllerAdvice {
    @Autowired
    private ResponseEntityFactory responseEntityFactory;

    @ExceptionHandler(DuplicateUserException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ApiError> handleDuplicateUserException(DuplicateUserException ex) {
        return responseEntityFactory.errorResponse(ex, HttpStatus.CONFLICT);
    }

}