package com.pkfare.tripscale.exception;

/**
 * Exception thrown when a requested resource is not found.
 * This typically results in a 404 HTTP status code.
 */
public class ResourceNotFoundException extends BusinessException {
    
    /**
     * Constructor with resource type and identifier
     */
    public ResourceNotFoundException(String resourceType, String identifier) {
        super(String.format("%s with identifier '%s' not found", resourceType, identifier), "RESOURCE_NOT_FOUND");
    }
    
    /**
     * Constructor with custom message
     */
    public ResourceNotFoundException(String message) {
        super(message, "RESOURCE_NOT_FOUND");
    }
    
    /**
     * Constructor with message and cause
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, "RESOURCE_NOT_FOUND", cause);
    }
}