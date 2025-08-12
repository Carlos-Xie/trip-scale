package com.pkfare.tripscale.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

/**
 * Domain model representing user inspirations for travel recommendations
 */
public class Inspirations {
    
    @Valid
    private List<RecentFocus> recentFocus;
    
    @Valid
    private List<LastVisit> last5YearVisits;
    
    private List<String> travelStyle;
    
    @NotNull(message = "Age is required")
    @Min(value = 1, message = "Age must be at least 1")
    private Integer age;
    
    public Inspirations() {}
    
    public Inspirations(List<RecentFocus> recentFocus, List<LastVisit> last5YearVisits, 
                       List<String> travelStyle, Integer age) {
        this.recentFocus = recentFocus;
        this.last5YearVisits = last5YearVisits;
        this.travelStyle = travelStyle;
        this.age = age;
    }
    
    public List<RecentFocus> getRecentFocus() {
        return recentFocus;
    }
    
    public void setRecentFocus(List<RecentFocus> recentFocus) {
        this.recentFocus = recentFocus;
    }
    
    public List<LastVisit> getLast5YearVisits() {
        return last5YearVisits;
    }
    
    public void setLast5YearVisits(List<LastVisit> last5YearVisits) {
        this.last5YearVisits = last5YearVisits;
    }
    
    public List<String> getTravelStyle() {
        return travelStyle;
    }
    
    public void setTravelStyle(List<String> travelStyle) {
        this.travelStyle = travelStyle;
    }
    
    public Integer getAge() {
        return age;
    }
    
    public void setAge(Integer age) {
        this.age = age;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Inspirations that = (Inspirations) o;
        return Objects.equals(recentFocus, that.recentFocus) &&
               Objects.equals(last5YearVisits, that.last5YearVisits) &&
               Objects.equals(travelStyle, that.travelStyle) &&
               Objects.equals(age, that.age);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(recentFocus, last5YearVisits, travelStyle, age);
    }
    
    @Override
    public String toString() {
        return "Inspirations{" +
               "recentFocus=" + recentFocus +
               ", last5YearVisits=" + last5YearVisits +
               ", travelStyle=" + travelStyle +
               ", age=" + age +
               '}';
    }
}