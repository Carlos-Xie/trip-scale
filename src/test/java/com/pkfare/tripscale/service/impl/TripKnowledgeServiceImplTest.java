package com.pkfare.tripscale.service.impl;

import com.pkfare.tripscale.config.TripKnowledgeServiceConfig;
import com.pkfare.tripscale.model.PersonalPreferences;
import com.pkfare.tripscale.model.TravelDemand;
import com.pkfare.tripscale.model.TripRoute;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class TripKnowledgeServiceImplTest {
    
    @Mock
    private TripKnowledgeServiceConfig config;
    
    private TripKnowledgeServiceImpl tripKnowledgeService;
    
    @BeforeEach
    void setUp() {
        lenient().when(config.getBaseUrl()).thenReturn("http://localhost:8003");
        lenient().when(config.getApiKey()).thenReturn("test-api-key");
        lenient().when(config.getMaxRetries()).thenReturn(3);
        lenient().when(config.getRetryDelayMs()).thenReturn(500);
        
        tripKnowledgeService = new TripKnowledgeServiceImpl(config);
    }
    
    @Test
    void findSuitableRoutes_Success() {
        // Arrange
        TravelDemand travelDemand = createTestTravelDemand();
        PersonalPreferences preferences = createTestPersonalPreferences();
        
        // Act
        List<TripRoute> result = tripKnowledgeService.findSuitableRoutes(travelDemand, preferences);
        
        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        
        // Verify routes are within reasonable day range
        for (TripRoute route : result) {
            assertTrue(route.getRecommendedDays() <= travelDemand.getDays() + 2);
            assertNotNull(route.getRouteId());
            assertNotNull(route.getDestinations());
            assertNotNull(route.getHighlights());
            assertTrue(route.getMatchScore() > 0 && route.getMatchScore() <= 1.0);
        }
    }
    
    @Test
    void findSuitableRoutes_WithLongTripDuration() {
        // Arrange
        TravelDemand travelDemand = createTestTravelDemand();
        travelDemand.setDays(20); // Long trip
        PersonalPreferences preferences = createTestPersonalPreferences();
        
        // Act
        List<TripRoute> result = tripKnowledgeService.findSuitableRoutes(travelDemand, preferences);
        
        // Assert
        assertNotNull(result);
        // Should return all routes since they all fit within the duration
        assertTrue(result.size() >= 3);
    }
    
    @Test
    void findSuitableRoutes_WithShortTripDuration() {
        // Arrange
        TravelDemand travelDemand = createTestTravelDemand();
        travelDemand.setDays(5); // Short trip
        PersonalPreferences preferences = createTestPersonalPreferences();
        
        // Act
        List<TripRoute> result = tripKnowledgeService.findSuitableRoutes(travelDemand, preferences);
        
        // Assert
        assertNotNull(result);
        // Should filter out longer routes
        for (TripRoute route : result) {
            assertTrue(route.getRecommendedDays() <= 7); // 5 + 2 flexibility
        }
    }
    
    @Test
    void filterRoutesByPreferences_WithHates() {
        // Arrange
        List<TripRoute> routes = createMockRoutes();
        PersonalPreferences preferences = new PersonalPreferences();
        preferences.setLikes(Arrays.asList("Cultural experiences", "Local cuisine"));
        preferences.setHates(Arrays.asList("Adventure sports", "Crowded places"));
        
        // Act
        List<TripRoute> result = tripKnowledgeService.filterRoutesByPreferences(routes, preferences);
        
        // Assert
        assertNotNull(result);
        // Should filter out routes with adventure sports
        for (TripRoute route : result) {
            boolean hasHatedElement = route.getHighlights().stream()
                .anyMatch(highlight -> preferences.getHates().stream()
                    .anyMatch(hate -> highlight.toLowerCase().contains(hate.toLowerCase())));
            assertFalse(hasHatedElement, "Route should not contain hated elements: " + route.getHighlights());
        }
    }
    
    @Test
    void filterRoutesByPreferences_WithNullPreferences() {
        // Arrange
        List<TripRoute> routes = createMockRoutes();
        
        // Act
        List<TripRoute> result = tripKnowledgeService.filterRoutesByPreferences(routes, null);
        
        // Assert
        assertNotNull(result);
        assertEquals(routes.size(), result.size());
    }
    
    @Test
    void filterRoutesByPreferences_WithNullHates() {
        // Arrange
        List<TripRoute> routes = createMockRoutes();
        PersonalPreferences preferences = new PersonalPreferences();
        preferences.setLikes(Arrays.asList("Cultural experiences"));
        preferences.setHates(null);
        
        // Act
        List<TripRoute> result = tripKnowledgeService.filterRoutesByPreferences(routes, preferences);
        
        // Assert
        assertNotNull(result);
        assertEquals(routes.size(), result.size());
    }
    
    @Test
    void isServiceHealthy_ReturnsTrue() {
        // Act
        boolean result = tripKnowledgeService.isServiceHealthy();
        
        // Assert
        assertTrue(result);
    }
    
    @Test
    void findSuitableRoutes_VerifyRouteContent() {
        // Arrange
        TravelDemand travelDemand = createTestTravelDemand();
        PersonalPreferences preferences = createTestPersonalPreferences();
        
        // Act
        List<TripRoute> result = tripKnowledgeService.findSuitableRoutes(travelDemand, preferences);
        
        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        
        // Verify first route has expected structure
        TripRoute firstRoute = result.get(0);
        assertNotNull(firstRoute.getRouteId());
        assertNotNull(firstRoute.getDestinations());
        assertFalse(firstRoute.getDestinations().isEmpty());
        assertNotNull(firstRoute.getEstimatedBudget());
        assertNotNull(firstRoute.getHighlights());
        assertFalse(firstRoute.getHighlights().isEmpty());
        assertTrue(firstRoute.getRecommendedDays() > 0);
        assertTrue(firstRoute.getMatchScore() > 0);
    }
    
    private TravelDemand createTestTravelDemand() {
        TravelDemand demand = new TravelDemand();
        demand.setMustGoDestinations(Arrays.asList("Tokyo", "Kyoto"));
        demand.setDays(10);
        demand.setPassenger(2);
        demand.setPassengerType("Adult");
        demand.setBudgets("$3000-4000");
        demand.setSessionId("test-session-123");
        return demand;
    }
    
    private PersonalPreferences createTestPersonalPreferences() {
        PersonalPreferences preferences = new PersonalPreferences();
        preferences.setLikes(Arrays.asList("Cultural experiences", "Local cuisine", "Museums and galleries"));
        preferences.setHates(Arrays.asList("Crowded tourist traps", "Extreme sports"));
        return preferences;
    }
    
    private List<TripRoute> createMockRoutes() {
        TripRoute route1 = new TripRoute();
        route1.setRouteId("TEST-001");
        route1.setDestinations(Arrays.asList("Tokyo", "Kyoto"));
        route1.setRecommendedDays(7);
        route1.setEstimatedBudget("$2500-3500");
        route1.setHighlights(Arrays.asList("Cultural experiences", "Local cuisine"));
        route1.setMatchScore(0.9);
        
        TripRoute route2 = new TripRoute();
        route2.setRouteId("TEST-002");
        route2.setDestinations(Arrays.asList("Queenstown", "Auckland"));
        route2.setRecommendedDays(10);
        route2.setEstimatedBudget("$4000-5000");
        route2.setHighlights(Arrays.asList("Adventure sports", "Mountain views"));
        route2.setMatchScore(0.8);
        
        return Arrays.asList(route1, route2);
    }
}