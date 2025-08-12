package com.pkfare.tripscale.dto;

import jakarta.validation.constraints.*;
import java.util.Objects;

/**
 * Request DTO for collecting travel details
 */
public class TravelDetailsRequest {
    
    @NotBlank(message = "Session ID is required")
    @Pattern(regexp = "^[a-zA-Z0-9\\-_]+$", message = "Session ID contains invalid characters")
    @Size(min = 1, max = 100, message = "Session ID must be between 1 and 100 characters")
    private String sessionId;
    
    @NotNull(message = "Days is required")
    @Min(value = 1, message = "Days must be at least 1")
    @Max(value = 365, message = "Days cannot exceed 365")
    private Integer days;
    
    @NotNull(message = "Passenger count is required")
    @Min(value = 1, message = "Passenger count must be at least 1")
    @Max(value = 50, message = "Passenger count cannot exceed 50")
    private Integer passenger;
    
    @NotBlank(message = "Passenger type is required")
    @Pattern(regexp = "^(adult|child|senior|family|group)$", 
             message = "Passenger type must be one of: adult, child, senior, family, group")
    private String passengerType;
    
    @NotBlank(message = "Budget is required")
    @Pattern(regexp = "^(low|medium|high|luxury)$", 
             message = "Budget must be one of: low, medium, high, luxury")
    private String budgets;
    
    public TravelDetailsRequest() {}
    
    public TravelDetailsRequest(String sessionId, Integer days, Integer passenger, 
                               String passengerType, String budgets) {
        this.sessionId = sessionId;
        this.days = days;
        this.passenger = passenger;
        this.passengerType = passengerType;
        this.budgets = budgets;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public Integer getDays() {
        return days;
    }
    
    public void setDays(Integer days) {
        this.days = days;
    }
    
    public Integer getPassenger() {
        return passenger;
    }
    
    public void setPassenger(Integer passenger) {
        this.passenger = passenger;
    }
    
    public String getPassengerType() {
        return passengerType;
    }
    
    public void setPassengerType(String passengerType) {
        this.passengerType = passengerType;
    }
    
    public String getBudgets() {
        return budgets;
    }
    
    public void setBudgets(String budgets) {
        this.budgets = budgets;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TravelDetailsRequest that = (TravelDetailsRequest) o;
        return Objects.equals(sessionId, that.sessionId) &&
               Objects.equals(days, that.days) &&
               Objects.equals(passenger, that.passenger) &&
               Objects.equals(passengerType, that.passengerType) &&
               Objects.equals(budgets, that.budgets);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(sessionId, days, passenger, passengerType, budgets);
    }
    
    @Override
    public String toString() {
        return "TravelDetailsRequest{" +
               "sessionId='" + sessionId + '\'' +
               ", days=" + days +
               ", passenger=" + passenger +
               ", passengerType='" + passengerType + '\'' +
               ", budgets='" + budgets + '\'' +
               '}';
    }
}