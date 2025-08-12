package com.pkfare.tripscale.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TravelServiceExceptionTest {

    @Test
    void constructor_WithMessage_ShouldSetDefaultErrorCode() {
        // Given
        String message = "Travel service error";

        // When
        TravelServiceException exception = new TravelServiceException(message);

        // Then
        assertEquals(message, exception.getMessage());
        assertEquals("TRAVEL_SERVICE_ERROR", exception.getErrorCode());
        assertNull(exception.getCause());
    }

    @Test
    void constructor_WithMessageAndErrorCode_ShouldSetBoth() {
        // Given
        String message = "Custom travel error";
        String errorCode = "CUSTOM_ERROR";

        // When
        TravelServiceException exception = new TravelServiceException(message, errorCode);

        // Then
        assertEquals(message, exception.getMessage());
        assertEquals(errorCode, exception.getErrorCode());
        assertNull(exception.getCause());
    }

    @Test
    void constructor_WithMessageAndCause_ShouldSetDefaultErrorCode() {
        // Given
        String message = "Travel service error";
        Throwable cause = new RuntimeException("Root cause");

        // When
        TravelServiceException exception = new TravelServiceException(message, cause);

        // Then
        assertEquals(message, exception.getMessage());
        assertEquals("TRAVEL_SERVICE_ERROR", exception.getErrorCode());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void constructor_WithAllParameters_ShouldSetAll() {
        // Given
        String message = "Custom travel error";
        String errorCode = "CUSTOM_ERROR";
        Throwable cause = new RuntimeException("Root cause");

        // When
        TravelServiceException exception = new TravelServiceException(message, errorCode, cause);

        // Then
        assertEquals(message, exception.getMessage());
        assertEquals(errorCode, exception.getErrorCode());
        assertEquals(cause, exception.getCause());
    }
}