package com.pkfare.tripscale.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

/**
 * Domain model representing travel demand information
 */
public class TravelDemand {
    
    @NotEmpty(message = "Must-go destinations cannot be empty")
    private List<String> mustGoDestinations;
    
    @NotNull(message = "Days is required")
    @Min(value = 1, message = "Days must be at least 1")
    private Integer days;
    
    @NotNull(message = "Passenger count is required")
    @Min(value = 1, message = "Passenger count must be at least 1")
    private Integer passenger;
    
    @NotBlank(message = "Passenger type is required")
    private String passengerType;
    
    @NotBlank(message = "Budget is required")
    private String budgets;
    
    private String sessionId;
    
    public TravelDemand() {}
    
    public TravelDemand(List<String> mustGoDestinations, Integer days, Integer passenger, 
                       String passengerType, String budgets, String sessionId) {
        this.mustGoDestinations = mustGoDestinations;
        this.days = days;
        this.passenger = passenger;
        this.passengerType = passengerType;
        this.budgets = budgets;
        this.sessionId = sessionId;
    }
    
    // Getters and Setters
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
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TravelDemand that = (TravelDemand) o;
        return Objects.equals(mustGoDestinations, that.mustGoDestinations) &&
               Objects.equals(days, that.days) &&
               Objects.equals(passenger, that.passenger) &&
               Objects.equals(passengerType, that.passengerType) &&
               Objects.equals(budgets, that.budgets) &&
               Objects.equals(sessionId, that.sessionId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(mustGoDestinations, days, passenger, passengerType, budgets, sessionId);
    }
    
    @Override
    public String toString() {
        return "TravelDemand{" +
               "mustGoDestinations=" + mustGoDestinations +
               ", days=" + days +
               ", passenger=" + passenger +
               ", passengerType='" + passengerType + '\'' +
               ", budgets='" + budgets + '\'' +
               ", sessionId='" + sessionId + '\'' +
               '}';
    }
}