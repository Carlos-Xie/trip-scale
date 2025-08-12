package com.pkfare.tripscale.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.util.List;
import java.util.Arrays;

/**
 * Configuration for mock data used in MemoryService implementation
 * Allows customization of mock data through application properties
 */
@Configuration
@ConfigurationProperties(prefix = "mock.memory")
public class MockDataConfig {
    
    private UserData userData = new UserData();
    
    public UserData getUserData() {
        return userData;
    }
    
    public void setUserData(UserData userData) {
        this.userData = userData;
    }
    
    public static class UserData {
        private List<String> recentFocusDestinations = Arrays.asList("Japan", "Europe", "Southeast Asia");
        private List<String> travelStyles = Arrays.asList("Cultural", "Adventure", "Relaxation", "Food & Drink");
        private List<String> likes = Arrays.asList(
            "Museums and galleries", 
            "Local cuisine", 
            "Nature and hiking", 
            "Photography", 
            "Historical sites",
            "Local markets",
            "Beach activities"
        );
        private List<String> hates = Arrays.asList(
            "Crowded tourist traps", 
            "Extreme sports", 
            "Long flights over 12 hours",
            "Very expensive restaurants",
            "Rainy weather destinations"
        );
        private int age = 28;
        
        // Getters and setters
        public List<String> getRecentFocusDestinations() {
            return recentFocusDestinations;
        }
        
        public void setRecentFocusDestinations(List<String> recentFocusDestinations) {
            this.recentFocusDestinations = recentFocusDestinations;
        }
        
        public List<String> getTravelStyles() {
            return travelStyles;
        }
        
        public void setTravelStyles(List<String> travelStyles) {
            this.travelStyles = travelStyles;
        }
        
        public List<String> getLikes() {
            return likes;
        }
        
        public void setLikes(List<String> likes) {
            this.likes = likes;
        }
        
        public List<String> getHates() {
            return hates;
        }
        
        public void setHates(List<String> hates) {
            this.hates = hates;
        }
        
        public int getAge() {
            return age;
        }
        
        public void setAge(int age) {
            this.age = age;
        }
    }
}