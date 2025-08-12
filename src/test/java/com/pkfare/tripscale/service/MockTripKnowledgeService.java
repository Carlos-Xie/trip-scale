package com.pkfare.tripscale.service;

import com.pkfare.tripscale.model.PersonalPreferences;
import com.pkfare.tripscale.model.TravelDemand;
import com.pkfare.tripscale.model.TripRoute;
import org.springframework.boot.test.context.TestComponent;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mock implementation of TripKnowledgeService for testing purposes
 */
@TestComponent
public class MockTripKnowledgeService implements TripKnowledgeService {
    
    private boolean serviceHealthy = true;
    
    @Override
    public List<TripRoute> findSuitableRoutes(TravelDemand travelDemand, PersonalPreferences personalPreferences) {
        // Create mock trip routes based on travel demand
        TripRoute route1 = new TripRoute();
        route1.setRouteId("route-001");
        route1.setDestinations(Arrays.asList("Tokyo", "Kyoto", "Osaka"));
        route1.setRecommendedDays(7);
        route1.setEstimatedBudget("$2500-3000");
        route1.setHighlights(Arrays.asList("Cultural temples", "Modern city experience", "Traditional cuisine"));
        route1.setMatchScore(0.92);
        
        TripRoute route2 = new TripRoute();
        route2.setRouteId("route-002");
        route2.setDestinations(Arrays.asList("Paris", "Lyon", "Nice"));
        route2.setRecommendedDays(8);
        route2.setEstimatedBudget("$2800-3500");
        route2.setHighlights(Arrays.asList("Art museums", "French cuisine", "Mediterranean coast"));
        route2.setMatchScore(0.87);
        
        TripRoute route3 = new TripRoute();
        route3.setRouteId("route-003");
        route3.setDestinations(Arrays.asList("Ubud", "Seminyak", "Nusa Penida"));
        route3.setRecommendedDays(6);
        route3.setEstimatedBudget("$1500-2000");
        route3.setHighlights(Arrays.asList("Natural beauty", "Relaxation", "Local culture"));
        route3.setMatchScore(0.81);
        
        List<TripRoute> allRoutes = Arrays.asList(route1, route2, route3);
        
        // Apply preference filtering
        return filterRoutesByPreferences(allRoutes, personalPreferences);
    }
    
    @Override
    public List<TripRoute> filterRoutesByPreferences(List<TripRoute> routes, PersonalPreferences personalPreferences) {
        if (personalPreferences == null || personalPreferences.getHates() == null) {
            return routes;
        }
        
        // Simple mock filtering - remove routes that contain hated elements
        return routes.stream()
                .filter(route -> {
                    boolean hasHatedElement = route.getHighlights().stream()
                            .anyMatch(highlight -> personalPreferences.getHates().stream()
                                    .anyMatch(hate -> highlight.toLowerCase().contains(hate.toLowerCase())));
                    return !hasHatedElement;
                })
                .collect(Collectors.toList());
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