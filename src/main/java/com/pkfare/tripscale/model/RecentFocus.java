package com.pkfare.tripscale.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Model representing recent focus destination with priority
 */
public class RecentFocus {
    
    @NotNull(message = "Priority is required")
    @Min(value = 1, message = "Priority must be at least 1")
    private Integer priority;
    
    @NotBlank(message = "Destination is required")
    private String destination;
    
    public RecentFocus() {}
    
    public RecentFocus(Integer priority, String destination) {
        this.priority = priority;
        this.destination = destination;
    }
    
    public Integer getPriority() {
        return priority;
    }
    
    public void setPriority(Integer priority) {
        this.priority = priority;
    }
    
    public String getDestination() {
        return destination;
    }
    
    public void setDestination(String destination) {
        this.destination = destination;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecentFocus that = (RecentFocus) o;
        return Objects.equals(priority, that.priority) &&
               Objects.equals(destination, that.destination);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(priority, destination);
    }
    
    @Override
    public String toString() {
        return "RecentFocus{" +
               "priority=" + priority +
               ", destination='" + destination + '\'' +
               '}';
    }
}