package com.example.framework.model;

import java.time.LocalDateTime;

/**
 * Standardized error response class for consistent error handling
 * across all API endpoints.
 */
public class ErrorResponse {
    
    private String message;
    private String error;
    private int status;
    private LocalDateTime timestamp;
    private String path;
    
    /**
     * Default constructor
     */
    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * Constructor with essential fields
     */
    public ErrorResponse(String message, String error, int status, String path) {
        this.message = message;
        this.error = error;
        this.status = status;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * Constructor with all fields
     */
    public ErrorResponse(String message, String error, int status, LocalDateTime timestamp, String path) {
        this.message = message;
        this.error = error;
        this.status = status;
        this.timestamp = timestamp;
        this.path = path;
    }
    
    // Getters
    public String getMessage() {
        return message;
    }
    
    public String getError() {
        return error;
    }
    
    public int getStatus() {
        return status;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public String getPath() {
        return path;
    }
    
    // Setters
    public void setMessage(String message) {
        this.message = message;
    }
    
    public void setError(String error) {
        this.error = error;
    }
    
    public void setStatus(int status) {
        this.status = status;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    @Override
    public String toString() {
        return "ErrorResponse{" +
                "message='" + message + '\'' +
                ", error='" + error + '\'' +
                ", status=" + status +
                ", timestamp=" + timestamp +
                ", path='" + path + '\'' +
                '}';
    }
}