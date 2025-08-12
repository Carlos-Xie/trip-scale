package com.pkfare.tripscale.exception;

/**
 * Exception thrown when no suitable routes are available for the given criteria.
 * This typically results in a 200 OK response with empty results and alternative suggestions.
 */
public class NoRoutesFoundException extends TravelServiceException {
    
    /**
     * Constructor with search criteria message
     */
    public NoRoutesFoundException(String searchCriteria) {
        super(String.format("No suitable routes found for criteria: %s", searchCriteria), "NO_ROUTES_FOUND");
    }
    
    /**
     * Constructor with custom message
     */
    public NoRoutesFoundException(String message, String errorCode) {
        super(message, errorCode);
    }
    
    /**
     * Constructor with message and cause
     */
    public NoRoutesFoundException(String message, Throwable cause) {
        super(message, "NO_ROUTES_FOUND", cause);
    }
}