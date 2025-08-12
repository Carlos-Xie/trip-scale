package com.pkfare.tripscale.exception;

/**
 * Exception thrown when external service communication fails.
 * This includes failures with Dify AI, Memory storage, and Trip Knowledge Base.
 */
public class ExternalServiceException extends TravelServiceException {
    
    private final String serviceName;
    
    /**
     * Constructor with service name and message
     */
    public ExternalServiceException(String serviceName, String message) {
        super(String.format("External service '%s' error: %s", serviceName, message), "EXTERNAL_SERVICE_ERROR");
        this.serviceName = serviceName;
    }
    
    /**
     * Constructor with service name, message and cause
     */
    public ExternalServiceException(String serviceName, String message, Throwable cause) {
        super(String.format("External service '%s' error: %s", serviceName, message), "EXTERNAL_SERVICE_ERROR", cause);
        this.serviceName = serviceName;
    }
    
    /**
     * Constructor with service name, message, error code and cause
     */
    public ExternalServiceException(String serviceName, String message, String errorCode, Throwable cause) {
        super(String.format("External service '%s' error: %s", serviceName, message), errorCode, cause);
        this.serviceName = serviceName;
    }
    
    public String getServiceName() {
        return serviceName;
    }
}