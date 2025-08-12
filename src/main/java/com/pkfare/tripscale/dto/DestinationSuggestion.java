package com.pkfare.tripscale.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import java.util.Objects;

/**
 * DTO representing a destination suggestion from AI
 */
public class DestinationSuggestion {
    
    @NotBlank(message = "Destination is required")
    private String destination;
    
    private String reason;
    
    @DecimalMin(value = "0.0", message = "Confidence must be at least 0.0")
    @DecimalMax(value = "1.0", message = "Confidence must be at most 1.0")
    private Double confidence;
    
    public DestinationSuggestion() {}
    
    public DestinationSuggestion(String destination, String reason, Double confidence) {
        this.destination = destination;
        this.reason = reason;
        this.confidence = confidence;
    }
    
    public String getDestination() {
        return destination;
    }
    
    public void setDestination(String destination) {
        this.destination = destination;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public Double getConfidence() {
        return confidence;
    }
    
    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DestinationSuggestion that = (DestinationSuggestion) o;
        return Objects.equals(destination, that.destination) &&
               Objects.equals(reason, that.reason) &&
               Objects.equals(confidence, that.confidence);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(destination, reason, confidence);
    }
    
    @Override
    public String toString() {
        return "DestinationSuggestion{" +
               "destination='" + destination + '\'' +
               ", reason='" + reason + '\'' +
               ", confidence=" + confidence +
               '}';
    }
}