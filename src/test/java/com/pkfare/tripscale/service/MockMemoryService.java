package com.pkfare.tripscale.service;

import com.pkfare.tripscale.model.*;
import org.springframework.boot.test.context.TestComponent;
import java.util.Arrays;

/**
 * Mock implementation of MemoryService for testing purposes
 */
@TestComponent
public class MockMemoryService implements MemoryService {
    
    private boolean serviceHealthy = true;
    
    @Override
    public Inspirations getInspirations(String userId) {
        Inspirations inspirations = new Inspirations();
        
        // Mock recent focus data
        RecentFocus focus1 = new RecentFocus();
        focus1.setPriority(1);
        focus1.setDestination("Japan");
        
        RecentFocus focus2 = new RecentFocus();
        focus2.setPriority(2);
        focus2.setDestination("Europe");
        
        inspirations.setRecentFocus(Arrays.asList(focus1, focus2));
        
        // Mock last 5 year visits
        LastVisit visit1 = new LastVisit();
        visit1.setDate("2023-06-15");
        visit1.setLocations(Arrays.asList("Bangkok", "Phuket"));
        
        LastVisit visit2 = new LastVisit();
        visit2.setDate("2022-12-20");
        visit2.setLocations(Arrays.asList("London", "Edinburgh"));
        
        inspirations.setLast5YearVisits(Arrays.asList(visit1, visit2));
        
        // Mock travel style and age
        inspirations.setTravelStyle(Arrays.asList("Cultural", "Adventure", "Relaxation"));
        inspirations.setAge(28);
        
        return inspirations;
    }
    
    @Override
    public PersonalPreferences getPersonalPreferences(String userId) {
        PersonalPreferences preferences = new PersonalPreferences();
        preferences.setLikes(Arrays.asList("Museums", "Local cuisine", "Nature", "Photography"));
        preferences.setHates(Arrays.asList("Crowded places", "Extreme sports", "Long flights"));
        return preferences;
    }
    
    @Override
    public void updateUserHistory(String userId, TravelDemand travelDemand) {
        // Mock implementation - in real service this would persist to storage
        // For testing, we just simulate successful update
    }
    
    @Override
    public boolean isServiceHealthy() {
        return serviceHealthy;
    }
    
    // Test helper methods
    public void setServiceHealthy(boolean healthy) {
        this.serviceHealthy = healthy;
    }
}