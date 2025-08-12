package com.pkfare.tripscale.service;

import com.pkfare.tripscale.model.PersonalPreferences;
import com.pkfare.tripscale.model.TravelDemand;
import com.pkfare.tripscale.model.TripRoute;
import java.util.List;

/**
 * Service interface for integrating with Trip Knowledge Base to search
 * and filter trip routes based on user demands and preferences.
 */
public interface TripKnowledgeService {
    
    /**
     * Find suitable trip routes based on travel demand and personal preferences
     * 
     * @param travelDemand User's travel requirements including destinations, days, passengers, and budget
     * @param personalPreferences User's likes and hates for filtering routes
     * @return List of TripRoute objects matching the criteria
     * @throws com.pkfare.tripscale.exception.BusinessException if search fails
     */
    List<TripRoute> findSuitableRoutes(TravelDemand travelDemand, PersonalPreferences personalPreferences);
    
    /**
     * Filter existing routes by personal preferences
     * 
     * @param routes List of trip routes to filter
     * @param personalPreferences User preferences for filtering
     * @return Filtered list of trip routes
     */
    List<TripRoute> filterRoutesByPreferences(List<TripRoute> routes, PersonalPreferences personalPreferences);
    
    /**
     * Check if the Trip Knowledge Base service is available and responding
     * 
     * @return true if service is healthy, false otherwise
     */
    boolean isServiceHealthy();
}