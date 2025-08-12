package com.pkfare.tripscale.service.impl;

import com.pkfare.tripscale.config.TripKnowledgeServiceConfig;
import com.pkfare.tripscale.exception.ExternalServiceException;
import com.pkfare.tripscale.exception.NoRoutesFoundException;
import com.pkfare.tripscale.exception.ValidationException;
import com.pkfare.tripscale.model.PersonalPreferences;
import com.pkfare.tripscale.model.TravelDemand;
import com.pkfare.tripscale.model.TripRoute;
import com.pkfare.tripscale.service.TripKnowledgeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mock implementation of TripKnowledgeService for development and testing purposes
 */
@Service
public class TripKnowledgeServiceImpl implements TripKnowledgeService {
    
    private static final Logger logger = LoggerFactory.getLogger(TripKnowledgeServiceImpl.class);
    
    private final TripKnowledgeServiceConfig config;
    
    @Autowired
    public TripKnowledgeServiceImpl(TripKnowledgeServiceConfig config) {
        this.config = config;
    }
    
    @Override
    public List<TripRoute> findSuitableRoutes(TravelDemand travelDemand, PersonalPreferences personalPreferences) {
        logger.info("Finding suitable routes for travel demand: {} destinations, {} days", 
                   travelDemand.getMustGoDestinations().size(), travelDemand.getDays());
        
        // Validate input parameters
        if (travelDemand == null) {
            throw new ValidationException("Travel demand cannot be null");
        }
        
        if (travelDemand.getMustGoDestinations() == null || travelDemand.getMustGoDestinations().isEmpty()) {
            throw new ValidationException("Must-go destinations cannot be null or empty");
        }
        
        if (travelDemand.getDays() == null || travelDemand.getDays() <= 0) {
            throw new ValidationException("Days must be a positive number");
        }
        
        try {
            // Simulate service unavailable for specific test scenarios
            if (travelDemand.getMustGoDestinations().contains("UNAVAILABLE")) {
                throw new ExternalServiceException("TripKnowledgeService", "Service temporarily unavailable");
            }
            
            // Mock implementation - return predefined routes
            List<TripRoute> mockRoutes = createMockRoutes();
            
            // Apply basic filtering based on travel demand
            List<TripRoute> filteredRoutes = mockRoutes.stream()
                .filter(route -> route.getRecommendedDays() <= travelDemand.getDays() + 2) // Allow some flexibility
                .collect(Collectors.toList());
            
            // Further filter by preferences
            List<TripRoute> finalRoutes = filterRoutesByPreferences(filteredRoutes, personalPreferences);
            
            // Check if no routes found
            if (finalRoutes.isEmpty()) {
                String criteria = String.format("destinations: %s, days: %d", 
                    travelDemand.getMustGoDestinations(), travelDemand.getDays());
                throw new NoRoutesFoundException(criteria);
            }
            
            logger.info("Found {} suitable routes", finalRoutes.size());
            return finalRoutes;
            
        } catch (NoRoutesFoundException | ValidationException e) {
            throw e;
        } catch (ExternalServiceException e) {
            logger.error("External service error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error finding suitable routes", e);
            throw new ExternalServiceException("TripKnowledgeService", "Failed to find suitable routes", e);
        }
    }
    
    @Override
    public List<TripRoute> filterRoutesByPreferences(List<TripRoute> routes, PersonalPreferences personalPreferences) {
        logger.info("Filtering {} routes by personal preferences", routes.size());
        
        if (routes == null) {
            throw new ValidationException("Routes list cannot be null");
        }
        
        if (personalPreferences == null || personalPreferences.getHates() == null) {
            logger.debug("No personal preferences to filter by, returning all routes");
            return routes;
        }
        
        try {
            // Simple mock filtering - remove routes that contain hated elements
            List<TripRoute> filteredRoutes = routes.stream()
                .filter(route -> {
                    boolean hasHatedElement = route.getHighlights().stream()
                        .anyMatch(highlight -> personalPreferences.getHates().stream()
                            .anyMatch(hate -> highlight.toLowerCase().contains(hate.toLowerCase())));
                    return !hasHatedElement;
                })
                .collect(Collectors.toList());
            
            logger.debug("Filtered {} routes to {} routes based on preferences", routes.size(), filteredRoutes.size());
            return filteredRoutes;
            
        } catch (Exception e) {
            logger.error("Error filtering routes by preferences", e);
            throw new ExternalServiceException("TripKnowledgeService", "Failed to filter routes by preferences", e);
        }
    }
    
    @Override
    public boolean isServiceHealthy() {
        // Mock implementation - always return true
        logger.debug("Trip Knowledge Base service health check - returning true (mock)");
        return true;
    }
    
    /**
     * Create mock trip routes for testing and development
     */
    private List<TripRoute> createMockRoutes() {
        List<TripRoute> routes = new ArrayList<>();
        
        // Route 1: Japan Cultural Tour
        TripRoute route1 = new TripRoute();
        route1.setRouteId("JP-CULTURAL-001");
        route1.setDestinations(Arrays.asList("Tokyo", "Kyoto", "Osaka"));
        route1.setRecommendedDays(7);
        route1.setEstimatedBudget("$2500-3500");
        route1.setHighlights(Arrays.asList("Traditional temples", "Cherry blossoms", "Local cuisine", "Cultural experiences"));
        route1.setMatchScore(0.92);
        routes.add(route1);
        
        // Route 2: European Adventure
        TripRoute route2 = new TripRoute();
        route2.setRouteId("EU-ADVENTURE-002");
        route2.setDestinations(Arrays.asList("Paris", "Amsterdam", "Berlin"));
        route2.setRecommendedDays(10);
        route2.setEstimatedBudget("$3000-4000");
        route2.setHighlights(Arrays.asList("Museums and galleries", "Historical sites", "Local markets", "Photography"));
        route2.setMatchScore(0.88);
        routes.add(route2);
        
        // Route 3: Southeast Asia Relaxation
        TripRoute route3 = new TripRoute();
        route3.setRouteId("SEA-RELAX-003");
        route3.setDestinations(Arrays.asList("Bali", "Bangkok", "Phuket"));
        route3.setRecommendedDays(12);
        route3.setEstimatedBudget("$2000-3000");
        route3.setHighlights(Arrays.asList("Beach activities", "Nature and hiking", "Local cuisine", "Relaxation"));
        route3.setMatchScore(0.85);
        routes.add(route3);
        
        // Route 4: New Zealand Nature
        TripRoute route4 = new TripRoute();
        route4.setRouteId("NZ-NATURE-004");
        route4.setDestinations(Arrays.asList("Auckland", "Queenstown", "Wellington"));
        route4.setRecommendedDays(14);
        route4.setEstimatedBudget("$4000-5500");
        route4.setHighlights(Arrays.asList("Mountain views", "Adventure sports", "Nature and hiking", "Photography"));
        route4.setMatchScore(0.80);
        routes.add(route4);
        
        // Route 5: Mediterranean Culture
        TripRoute route5 = new TripRoute();
        route5.setRouteId("MED-CULTURE-005");
        route5.setDestinations(Arrays.asList("Barcelona", "Rome", "Athens"));
        route5.setRecommendedDays(9);
        route5.setEstimatedBudget("$2800-3800");
        route5.setHighlights(Arrays.asList("Historical sites", "Local cuisine", "Museums and galleries", "Cultural experiences"));
        route5.setMatchScore(0.87);
        routes.add(route5);
        
        return routes;
    }
}