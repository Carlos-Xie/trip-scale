package com.pkfare.tripscale.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Objects;

/**
 * Response DTO for GuessMe feature
 */
public class GuessMeResponse {
    
    @NotBlank(message = "Session ID is required")
    private String sessionId;
    
    @Valid
    private List<DestinationSuggestion> suggestions;
    
    private String message;
    
    public GuessMeResponse() {}
    
    public GuessMeResponse(String sessionId, List<DestinationSuggestion> suggestions, String message) {
        this.sessionId = sessionId;
        this.suggestions = suggestions;
        this.message = message;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public List<DestinationSuggestion> getSuggestions() {
        return suggestions;
    }
    
    public void setSuggestions(List<DestinationSuggestion> suggestions) {
        this.suggestions = suggestions;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GuessMeResponse that = (GuessMeResponse) o;
        return Objects.equals(sessionId, that.sessionId) &&
               Objects.equals(suggestions, that.suggestions) &&
               Objects.equals(message, that.message);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(sessionId, suggestions, message);
    }
    
    @Override
    public String toString() {
        return "GuessMeResponse{" +
               "sessionId='" + sessionId + '\'' +
               ", suggestions=" + suggestions +
               ", message='" + message + '\'' +
               '}';
    }
}