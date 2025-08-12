package com.pkfare.tripscale.service;

import com.example.framework.Application;
import com.pkfare.tripscale.model.Inspirations;
import com.pkfare.tripscale.model.PersonalPreferences;
import com.pkfare.tripscale.model.TravelDemand;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for MemoryServiceImpl to verify mock data functionality
 */
@SpringBootTest(classes = {Application.class, com.pkfare.tripscale.service.impl.MemoryServiceImpl.class, com.pkfare.tripscale.config.MockDataConfig.class})
class MemoryServiceImplTest {
    
    @Autowired
    private MemoryService memoryService;
    
    @Test
    void testGetInspirations() {
        String userId = "test-user-123";
        
        Inspirations inspirations = memoryService.getInspirations(userId);
        
        assertNotNull(inspirations, "Inspirations should not be null");
        assertNotNull(inspirations.getRecentFocus(), "Recent focus should not be null");
        assertFalse(inspirations.getRecentFocus().isEmpty(), "Recent focus should not be empty");
        assertNotNull(inspirations.getLast5YearVisits(), "Last 5 year visits should not be null");
        assertNotNull(inspirations.getTravelStyle(), "Travel style should not be null");
        assertTrue(inspirations.getAge() > 0, "Age should be positive");
        
        // Verify specific mock data
        assertEquals("Japan", inspirations.getRecentFocus().get(0).getDestination());
        assertEquals(1, inspirations.getRecentFocus().get(0).getPriority());
        assertEquals(28, inspirations.getAge());
    }
    
    @Test
    void testGetPersonalPreferences() {
        String userId = "test-user-123";
        
        PersonalPreferences preferences = memoryService.getPersonalPreferences(userId);
        
        assertNotNull(preferences, "Personal preferences should not be null");
        assertNotNull(preferences.getLikes(), "Likes should not be null");
        assertNotNull(preferences.getHates(), "Hates should not be null");
        assertFalse(preferences.getLikes().isEmpty(), "Likes should not be empty");
        assertFalse(preferences.getHates().isEmpty(), "Hates should not be empty");
        
        // Verify some expected mock data
        assertTrue(preferences.getLikes().contains("Local cuisine"));
        assertTrue(preferences.getHates().contains("Crowded tourist traps"));
    }
    
    @Test
    void testUpdateUserHistory() {
        String userId = "test-user-123";
        TravelDemand travelDemand = new TravelDemand();
        travelDemand.setMustGoDestinations(java.util.Arrays.asList("Tokyo", "Kyoto"));
        travelDemand.setDays(7);
        travelDemand.setPassenger(2);
        travelDemand.setPassengerType("Adults");
        travelDemand.setBudgets("$2000-3000");
        
        // This should not throw any exception
        assertDoesNotThrow(() -> memoryService.updateUserHistory(userId, travelDemand));
    }
    
    @Test
    void testIsServiceHealthy() {
        assertTrue(memoryService.isServiceHealthy(), "Mock service should always be healthy");
    }
}