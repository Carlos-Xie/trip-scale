package com.pkfare.tripscale.service.impl;

import com.pkfare.tripscale.config.MockDataConfig;
import com.pkfare.tripscale.exception.ExternalServiceException;
import com.pkfare.tripscale.exception.UserNotFoundException;
import com.pkfare.tripscale.exception.ValidationException;
import com.pkfare.tripscale.model.*;
import com.pkfare.tripscale.service.MemoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Simple implementation of MemoryService with configurable mock data
 * for API development and testing purposes.
 */
@Service
public class MemoryServiceImpl implements MemoryService {
    
    private static final Logger logger = LoggerFactory.getLogger(MemoryServiceImpl.class);
    
    @Autowired
    private MockDataConfig mockDataConfig;
    
    @Override
    public Inspirations getInspirations(String userId) {
        logger.info("Retrieving inspirations for user: {}", userId);
        
        if (userId == null || userId.trim().isEmpty()) {
            throw new ValidationException("User ID cannot be null or empty");
        }
        
        try {
            // Simulate user not found scenario for specific test users
            if ("nonexistent".equals(userId)) {
                throw new UserNotFoundException(userId);
            }
            
            // Return configurable mock inspirations data
            Inspirations inspirations = new Inspirations();
        
        // Create recent focus data from configuration
        List<String> focusDestinations = mockDataConfig.getUserData().getRecentFocusDestinations();
        List<RecentFocus> recentFocusList = IntStream.range(0, focusDestinations.size())
                .mapToObj(i -> {
                    RecentFocus focus = new RecentFocus();
                    focus.setPriority(i + 1);
                    focus.setDestination(focusDestinations.get(i));
                    return focus;
                })
                .toList();
        
        inspirations.setRecentFocus(recentFocusList);
        
        // Mock last 5 year visits with predefined data
        LastVisit visit1 = new LastVisit();
        visit1.setDate("2023-06-15");
        visit1.setLocations(Arrays.asList("Bangkok", "Phuket", "Chiang Mai"));
        
        LastVisit visit2 = new LastVisit();
        visit2.setDate("2022-12-20");
        visit2.setLocations(Arrays.asList("London", "Edinburgh", "Dublin"));
        
        LastVisit visit3 = new LastVisit();
        visit3.setDate("2022-08-10");
        visit3.setLocations(Arrays.asList("Paris", "Lyon", "Nice"));
        
        inspirations.setLast5YearVisits(Arrays.asList(visit1, visit2, visit3));
        
            // Use configurable travel style and age
            inspirations.setTravelStyle(mockDataConfig.getUserData().getTravelStyles());
            inspirations.setAge(mockDataConfig.getUserData().getAge());
            
            logger.debug("Successfully retrieved inspirations for user: {}", userId);
            return inspirations;
            
        } catch (UserNotFoundException e) {
            logger.warn("User not found: {}", userId);
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving inspirations for user: {}", userId, e);
            throw new ExternalServiceException("MemoryService", "Failed to retrieve user inspirations", e);
        }
    }
    
    @Override
    public PersonalPreferences getPersonalPreferences(String userId) {
        logger.info("Retrieving personal preferences for user: {}", userId);
        
        if (userId == null || userId.trim().isEmpty()) {
            throw new ValidationException("User ID cannot be null or empty");
        }
        
        try {
            // Simulate user not found scenario for specific test users
            if ("nonexistent".equals(userId)) {
                throw new UserNotFoundException(userId);
            }
            
            // Return configurable personal preferences
            PersonalPreferences preferences = new PersonalPreferences();
            preferences.setLikes(mockDataConfig.getUserData().getLikes());
            preferences.setHates(mockDataConfig.getUserData().getHates());
            
            logger.debug("Successfully retrieved personal preferences for user: {}", userId);
            return preferences;
            
        } catch (UserNotFoundException e) {
            logger.warn("User not found: {}", userId);
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving personal preferences for user: {}", userId, e);
            throw new ExternalServiceException("MemoryService", "Failed to retrieve user preferences", e);
        }
    }
    
    @Override
    public void updateUserHistory(String userId, TravelDemand travelDemand) {
        logger.info("Updating user history for user: {}", userId);
        
        if (userId == null || userId.trim().isEmpty()) {
            throw new ValidationException("User ID cannot be null or empty");
        }
        
        if (travelDemand == null) {
            throw new ValidationException("Travel demand cannot be null");
        }
        
        try {
            // Simulate user not found scenario for specific test users
            if ("nonexistent".equals(userId)) {
                throw new UserNotFoundException(userId);
            }
            
            // Mock implementation - just log that we received the update
            logger.debug("Mock MemoryService: Received travel demand update for user {}", userId);
            logger.debug("Must-go destinations: {}", travelDemand.getMustGoDestinations());
            logger.debug("Days: {}", travelDemand.getDays());
            logger.debug("Passengers: {}", travelDemand.getPassenger());
            logger.debug("Budget: {}", travelDemand.getBudgets());
            
            // In a real implementation, this would persist to storage
            logger.info("Successfully updated user history for user: {}", userId);
            
        } catch (UserNotFoundException e) {
            logger.warn("User not found during history update: {}", userId);
            throw e;
        } catch (ValidationException e) {
            logger.warn("Validation error during history update: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error updating user history for user: {}", userId, e);
            throw new ExternalServiceException("MemoryService", "Failed to update user history", e);
        }
    }
    
    @Override
    public boolean isServiceHealthy() {
        // Always return true for mock implementation
        return true;
    }
}