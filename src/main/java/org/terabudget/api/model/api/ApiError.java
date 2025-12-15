package org.terabudget.api.model.api;

import java.util.List;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ApiError {
    private final HttpStatus status;
    private final String message;
    private final List<String> errors;
}