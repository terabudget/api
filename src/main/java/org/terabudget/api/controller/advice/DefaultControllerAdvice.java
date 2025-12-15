package org.terabudget.api.controller.advice;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.terabudget.api.factory.ResponseEntityFactory;
import org.terabudget.api.model.api.ApiError;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestControllerAdvice
@Slf4j
@Order(value = Ordered.LOWEST_PRECEDENCE)
public class DefaultControllerAdvice {
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
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiError> handleTokenRefreshException(Exception ex) {
        log.error("err", ex);
        return responseEntityFactory.errorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}