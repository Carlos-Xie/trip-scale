package com.pkfare.tripscale.dto;

import jakarta.validation.constraints.*;
import java.util.List;
import java.util.Objects;

/**
 * Request DTO for travel demand input
 */
public class TravelDemandRequest {
    
    @NotEmpty(message = "Must-go destinations cannot be empty")
    @Size(min = 1, max = 10, message = "Must have between 1 and 10 destinations")
    private List<@NotBlank(message = "Destination cannot be blank") 
                 @Size(max = 100, message = "Destination name too long") String> mustGoDestinations;
    
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
    
    @NotBlank(message = "User ID is required")
    @Pattern(regexp = "^[a-zA-Z0-9\\-_]+$", message = "User ID contains invalid characters")
    @Size(min = 1, max = 50, message = "User ID must be between 1 and 50 characters")
    private String userId;
    
    public TravelDemandRequest() {}
    
    public TravelDemandRequest(List<String> mustGoDestinations, Integer days, Integer passenger,
                              String passengerType, String budgets, String userId) {
        this.mustGoDestinations = mustGoDestinations;
        this.days = days;
        this.passenger = passenger;
        this.passengerType = passengerType;
        this.budgets = budgets;
        this.userId = userId;
    }
    
    public List<String> getMustGoDestinations() {
        return mustGoDestinations;
    }
    
    public void setMustGoDestinations(List<String> mustGoDestinations) {
        this.mustGoDestinations = mustGoDestinations;
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
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TravelDemandRequest that = (TravelDemandRequest) o;
        return Objects.equals(mustGoDestinations, that.mustGoDestinations) &&
               Objects.equals(days, that.days) &&
               Objects.equals(passenger, that.passenger) &&
               Objects.equals(passengerType, that.passengerType) &&
               Objects.equals(budgets, that.budgets) &&
               Objects.equals(userId, that.userId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(mustGoDestinations, days, passenger, passengerType, budgets, userId);
    }
    
    @Override
    public String toString() {
        return "TravelDemandRequest{" +
               "mustGoDestinations=" + mustGoDestinations +
               ", days=" + days +
               ", passenger=" + passenger +
               ", passengerType='" + passengerType + '\'' +
               ", budgets='" + budgets + '\'' +
               ", userId='" + userId + '\'' +
               '}';
    }
}