package ru.practicum.shareit.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ErrorMessageTest {

    @Test
    void testErrorMessageInitializationAndGetter() {
        String expectedError = "This is a test error message.";
        ErrorMessage errorMessage = new ErrorMessage(expectedError);
         
        assertNotNull(errorMessage, "ErrorMessage object should not be null.");
        assertEquals(expectedError, errorMessage.getError(), "getError should return the correct error message.");
    }
}