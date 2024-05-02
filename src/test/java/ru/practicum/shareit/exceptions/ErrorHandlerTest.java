package ru.practicum.shareit.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

class ErrorHandlerTest {

    @Test
    public void testHandleValidationException() {
        ValidationException validationException = mock(ValidationException.class);

        ErrorHandler errorHandler = new ErrorHandler();
        ResponseEntity<ErrorMessage> responseEntity = errorHandler.handleValidationException(validationException);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void testHandleNotFoundException() {
        ResourceNotFoundException notFoundException = mock(ResourceNotFoundException.class);

        ErrorHandler errorHandler = new ErrorHandler();
        ResponseEntity<ErrorMessage> responseEntity = errorHandler.handleNotFoundException(notFoundException);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void testHandleConflictException() {
        ConflictException conflictException = mock(ConflictException.class);

        ErrorHandler errorHandler = new ErrorHandler();
        ResponseEntity<ErrorMessage> responseEntity = errorHandler.handleConflictException(conflictException);

        assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
    }
}