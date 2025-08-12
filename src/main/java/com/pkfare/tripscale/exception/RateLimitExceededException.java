package com.pkfare.tripscale.exception;

/**
 * Exception thrown when rate limit is exceeded for external service calls.
 */
public class RateLimitExceededException extends RuntimeException {
    
    private final String service;
    private final String userId;
    private final long retryAfterSeconds;
    
    public RateLimitExceededException(String service, String userId, long retryAfterSeconds) {
        super(String.format("Rate limit exceeded for service '%s' and user '%s'. Retry after %d seconds.", 
              service, userId, retryAfterSeconds));
        this.service = service;
        this.userId = userId;
        this.retryAfterSeconds = retryAfterSeconds;
    }
    
    public String getService() {
        return service;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public long getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}