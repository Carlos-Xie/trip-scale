package com.example.framework.exception;

/**
 * Base class for all business-related exceptions in the application.
 * This exception represents errors that occur due to business rule violations
 * or invalid business operations.
 */
public class BusinessException extends RuntimeException {
    
    private final String errorCode;
    
    /**
     * Constructor with message only
     */
    public BusinessException(String message) {
        super(message);
        this.errorCode = "BUSINESS_ERROR";
    }
    
    /**
     * Constructor with message and error code
     */
    public BusinessException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    /**
     * Constructor with message and cause
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "BUSINESS_ERROR";
    }
    
    /**
     * Constructor with message, error code, and cause
     */
    public BusinessException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}