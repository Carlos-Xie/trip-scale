package com.pkfare.tripscale.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Objects;

/**
 * Request DTO for destination confirmation
 */
public class DestinationConfirmationRequest {
    
    @NotBlank(message = "Session ID is required")
    @Pattern(regexp = "^[a-zA-Z0-9\\-_]+$", message = "Session ID contains invalid characters")
    @Size(min = 1, max = 100, message = "Session ID must be between 1 and 100 characters")
    private String sessionId;
    
    @NotBlank(message = "Destination is required")
    @Size(max = 100, message = "Destination name too long")
    @Pattern(regexp = "^[a-zA-Z0-9\\s\\-'.,()]+$", message = "Destination contains invalid characters")
    private String destination;
    
    private boolean confirmed;
    
    public DestinationConfirmationRequest() {}
    
    public DestinationConfirmationRequest(String sessionId, String destination, boolean confirmed) {
        this.sessionId = sessionId;
        this.destination = destination;
        this.confirmed = confirmed;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public String getDestination() {
        return destination;
    }
    
    public void setDestination(String destination) {
        this.destination = destination;
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DestinationConfirmationRequest that = (DestinationConfirmationRequest) o;
        return confirmed == that.confirmed &&
               Objects.equals(sessionId, that.sessionId) &&
               Objects.equals(destination, that.destination);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(sessionId, destination, confirmed);
    }
    
    @Override
    public String toString() {
        return "DestinationConfirmationRequest{" +
               "sessionId='" + sessionId + '\'' +
               ", destination='" + destination + '\'' +
               ", confirmed=" + confirmed +
               '}';
    }
}