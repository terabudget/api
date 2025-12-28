package org.terabudget.api.factory;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.terabudget.api.model.api.ApiError;

@Component
public class ResponseEntityFactory {
    public ResponseEntity<ApiError> errorResponse(Exception ex, HttpStatus status) {
        List<String> errors = new ArrayList<String>();
        errors.add(ex.getMessage());
        ApiError error = ApiError.builder()
                .status(status)
                .message(status.name())
                .errors(List.of())
                .build();
        return ResponseEntity.status(status).body(error);
    }
}
