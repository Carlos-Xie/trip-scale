package com.pkfare.tripscale.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExternalServiceExceptionTest {

    @Test
    void constructor_WithServiceNameAndMessage_ShouldFormatMessage() {
        // Given
        String serviceName = "DifyService";
        String message = "Connection timeout";

        // When
        ExternalServiceException exception = new ExternalServiceException(serviceName, message);

        // Then
        assertEquals("External service 'DifyService' error: Connection timeout", exception.getMessage());
        assertEquals("EXTERNAL_SERVICE_ERROR", exception.getErrorCode());
        assertEquals(serviceName, exception.getServiceName());
        assertNull(exception.getCause());
    }

    @Test
    void constructor_WithServiceNameMessageAndCause_ShouldFormatMessage() {
        // Given
        String serviceName = "MemoryService";
        String message = "Database connection failed";
        Throwable cause = new RuntimeException("Connection refused");

        // When
        ExternalServiceException exception = new ExternalServiceException(serviceName, message, cause);

        // Then
        assertEquals("External service 'MemoryService' error: Database connection failed", exception.getMessage());
        assertEquals("EXTERNAL_SERVICE_ERROR", exception.getErrorCode());
        assertEquals(serviceName, exception.getServiceName());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void constructor_WithAllParameters_ShouldSetAll() {
        // Given
        String serviceName = "TripKnowledgeService";
        String message = "API rate limit exceeded";
        String errorCode = "RATE_LIMIT_EXCEEDED";
        Throwable cause = new RuntimeException("HTTP 429");

        // When
        ExternalServiceException exception = new ExternalServiceException(serviceName, message, errorCode, cause);

        // Then
        assertEquals("External service 'TripKnowledgeService' error: API rate limit exceeded", exception.getMessage());
        assertEquals(errorCode, exception.getErrorCode());
        assertEquals(serviceName, exception.getServiceName());
        assertEquals(cause, exception.getCause());
    }
}