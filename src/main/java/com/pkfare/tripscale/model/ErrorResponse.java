package com.pkfare.tripscale.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Standard error response model for API errors
 */
public class ErrorResponse {
    
    private String error;
    private String errorCode;
    private Integer status;
    private String path;
    private LocalDateTime timestamp;
    private List<String> details;
    
    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }
    
    public ErrorResponse(String error, String errorCode, Integer status, String path) {
        this.error = error;
        this.errorCode = errorCode;
        this.status = status;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }
    
    public ErrorResponse(String error, String errorCode, Integer status, String path, List<String> details) {
        this.error = error;
        this.errorCode = errorCode;
        this.status = status;
        this.path = path;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }
    
    public String getError() {
        return error;
    }
    
    public void setError(String error) {
        this.error = error;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public List<String> getDetails() {
        return details;
    }
    
    public void setDetails(List<String> details) {
        this.details = details;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ErrorResponse that = (ErrorResponse) o;
        return Objects.equals(error, that.error) &&
               Objects.equals(errorCode, that.errorCode) &&
               Objects.equals(status, that.status) &&
               Objects.equals(path, that.path) &&
               Objects.equals(timestamp, that.timestamp) &&
               Objects.equals(details, that.details);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(error, errorCode, status, path, timestamp, details);
    }
    
    @Override
    public String toString() {
        return "ErrorResponse{" +
               "error='" + error + '\'' +
               ", errorCode='" + errorCode + '\'' +
               ", status=" + status +
               ", path='" + path + '\'' +
               ", timestamp=" + timestamp +
               ", details=" + details +
               '}';
    }
}