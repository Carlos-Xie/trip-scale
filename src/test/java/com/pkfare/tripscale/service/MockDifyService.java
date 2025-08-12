package com.pkfare.tripscale.service;

import com.pkfare.tripscale.dto.DestinationSuggestion;
import com.pkfare.tripscale.dto.GuessMeResponse;
import com.pkfare.tripscale.model.Inspirations;
import org.springframework.boot.test.context.TestComponent;
import java.util.Arrays;
import java.util.UUID;

/**
 * Mock implementation of DifyService for testing purposes
 */
@TestComponent
public class MockDifyService implements DifyService {
    
    private boolean serviceHealthy = true;
    
    @Override
    public GuessMeResponse guessDestination(Inspirations inspirations) {
        GuessMeResponse response = new GuessMeResponse();
        response.setSessionId(UUID.randomUUID().toString());
        response.setMessage("AI-generated destination suggestions based on your preferences");
        
        // Create mock destination suggestions based on inspirations
        DestinationSuggestion suggestion1 = new DestinationSuggestion();
        suggestion1.setDestination("Tokyo, Japan");
        suggestion1.setReason("Based on your interest in cultural experiences and modern cities");
        suggestion1.setConfidence(0.85);
        
        DestinationSuggestion suggestion2 = new DestinationSuggestion();
        suggestion2.setDestination("Paris, France");
        suggestion2.setReason("Matches your preference for art and historical destinations");
        suggestion2.setConfidence(0.78);
        
        DestinationSuggestion suggestion3 = new DestinationSuggestion();
        suggestion3.setDestination("Bali, Indonesia");
        suggestion3.setReason("Perfect for relaxation and natural beauty experiences");
        suggestion3.setConfidence(0.72);
        
        response.setSuggestions(Arrays.asList(suggestion1, suggestion2, suggestion3));
        
        return response;
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