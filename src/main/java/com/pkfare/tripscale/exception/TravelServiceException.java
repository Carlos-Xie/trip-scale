package com.pkfare.tripscale.exception;

/**
 * Base exception for travel service errors.
 * This exception represents errors that occur during travel route determination processes.
 */
public class TravelServiceException extends BusinessException {
    
    /**
     * Constructor with message only
     */
    public TravelServiceException(String message) {
        super(message, "TRAVEL_SERVICE_ERROR");
    }
    
    /**
     * Constructor with message and error code
     */
    public TravelServiceException(String message, String errorCode) {
        super(message, errorCode);
    }
    
    /**
     * Constructor with message and cause
     */
    public TravelServiceException(String message, Throwable cause) {
        super(message, "TRAVEL_SERVICE_ERROR", cause);
    }
    
    /**
     * Constructor with message, error code, and cause
     */
    public TravelServiceException(String message, String errorCode, Throwable cause) {
        super(message, errorCode, cause);
    }
}