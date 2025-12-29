package org.terabudget.api.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.terabudget.api.exception.DuplicateUserException;
import org.terabudget.api.model.api.ApiError;

public class ResponseEntityFactoryTest {
    @Test
    public void errorResponse_success() {
        DuplicateUserException ex = Instancio.create(DuplicateUserException.class);
        ResponseEntityFactory factory = new ResponseEntityFactory();

        ResponseEntity<ApiError> entity = factory.errorResponse(ex, HttpStatus.BAD_REQUEST);

        ApiError expectedError = ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(HttpStatus.BAD_REQUEST.name())
                .errors(List.of())
                .build();

        assertEquals(entity.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertEquals(expectedError, entity.getBody());
    }
}
