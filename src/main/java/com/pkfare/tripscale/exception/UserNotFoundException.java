package com.pkfare.tripscale.exception;

/**
 * Exception thrown when user data is not found in Memory storage.
 * This typically results in a 404 HTTP status code.
 */
public class UserNotFoundException extends ResourceNotFoundException {
    
    /**
     * Constructor with user ID
     */
    public UserNotFoundException(String userId) {
        super("User", userId);
    }
    
    /**
     * Constructor with custom message
     */
    public UserNotFoundException(String message, String userId) {
        super(String.format("%s (User ID: %s)", message, userId));
    }
    
    /**
     * Constructor with message and cause
     */
    public UserNotFoundException(String userId, Throwable cause) {
        super(String.format("User with ID '%s' not found", userId), cause);
    }
}