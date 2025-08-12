package com.pkfare.tripscale.exception;

/**
 * Exception thrown when input validation fails.
 * This typically results in a 400 HTTP status code.
 */
public class ValidationException extends BusinessException {
    
    /**
     * Constructor with validation message
     */
    public ValidationException(String message) {
        super(message, "VALIDATION_ERROR");
    }
    
    /**
     * Constructor with field name and validation message
     */
    public ValidationException(String fieldName, String validationMessage) {
        super(String.format("Validation failed for field '%s': %s", fieldName, validationMessage), "VALIDATION_ERROR");
    }
    
    /**
     * Constructor with message and cause
     */
    public ValidationException(String message, Throwable cause) {
        super(message, "VALIDATION_ERROR", cause);
    }
}