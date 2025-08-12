package com.pkfare.tripscale.service;

import com.pkfare.tripscale.model.Inspirations;
import com.pkfare.tripscale.model.PersonalPreferences;
import com.pkfare.tripscale.model.TravelDemand;

/**
 * Service interface for integrating with Memory storage to retrieve user data
 * and store travel decisions.
 */
public interface MemoryService {
    
    /**
     * Retrieve user inspirations from Memory storage
     * 
     * @param userId The unique identifier for the user
     * @return Inspirations containing recent focus, past visits, travel style, and age
     * @throws com.pkfare.tripscale.exception.ResourceNotFoundException if user not found
     */
    Inspirations getInspirations(String userId);
    
    /**
     * Retrieve user personal preferences from Memory storage
     * 
     * @param userId The unique identifier for the user
     * @return PersonalPreferences containing likes and hates
     * @throws com.pkfare.tripscale.exception.ResourceNotFoundException if user not found
     */
    PersonalPreferences getPersonalPreferences(String userId);
    
    /**
     * Update user travel history with new travel decisions
     * 
     * @param userId The unique identifier for the user
     * @param travelDemand The travel demand to store in user history
     * @throws com.pkfare.tripscale.exception.BusinessException if update fails
     */
    void updateUserHistory(String userId, TravelDemand travelDemand);
    
    /**
     * Check if the Memory service is available and responding
     * 
     * @return true if service is healthy, false otherwise
     */
    boolean isServiceHealthy();
}