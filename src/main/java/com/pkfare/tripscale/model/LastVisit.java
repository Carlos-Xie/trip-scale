package com.pkfare.tripscale.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Objects;

/**
 * Model representing last visit information with date and locations
 */
public class LastVisit {
    
    @NotBlank(message = "Date is required")
    private String date;
    
    @NotEmpty(message = "Locations cannot be empty")
    private List<String> locations;
    
    public LastVisit() {}
    
    public LastVisit(String date, List<String> locations) {
        this.date = date;
        this.locations = locations;
    }
    
    public String getDate() {
        return date;
    }
    
    public void setDate(String date) {
        this.date = date;
    }
    
    public List<String> getLocations() {
        return locations;
    }
    
    public void setLocations(List<String> locations) {
        this.locations = locations;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LastVisit lastVisit = (LastVisit) o;
        return Objects.equals(date, lastVisit.date) &&
               Objects.equals(locations, lastVisit.locations);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(date, locations);
    }
    
    @Override
    public String toString() {
        return "LastVisit{" +
               "date='" + date + '\'' +
               ", locations=" + locations +
               '}';
    }
}