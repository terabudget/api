package org.terabudget.api.controller.advice;

import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.terabudget.api.factory.ResponseEntityFactory;
import org.terabudget.api.model.api.ApiError;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;

@RestControllerAdvice
@Slf4j
public class ContentControllerAdvice {
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
    @ExceptionHandler({ HttpMediaTypeNotSupportedException.class, HttpMessageNotReadableException.class,
            MethodArgumentNotValidException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> handleIncorrectContent(Exception ex) {
        log.error("err", ex);
        return responseEntityFactory.errorResponse(ex, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle not found resources
     *
     * @param ex
     * @return
     */
    @ExceptionHandler({ MissingServletRequestParameterException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> handleNotFound(Exception ex) {
        log.error("err", ex);
        return responseEntityFactory.errorResponse(ex, HttpStatus.NOT_FOUND);
    }
}