package com.pkfare.tripscale.service;

import com.pkfare.tripscale.dto.GuessMeResponse;
import com.pkfare.tripscale.model.Inspirations;

/**
 * Service interface for integrating with Dify AI API to generate destination suggestions
 * based on user inspirations and preferences.
 */
public interface DifyService {
    
    /**
     * Generate destination suggestions based on user inspirations
     * 
     * @param inspirations User's travel inspirations including recent focus, past visits, and preferences
     * @return GuessMeResponse containing destination suggestions with confidence scores
     * @throws com.pkfare.tripscale.exception.BusinessException if Dify API call fails
     */
    GuessMeResponse guessDestination(Inspirations inspirations);
    
    /**
     * Check if the Dify service is available and responding
     * 
     * @return true if service is healthy, false otherwise
     */
    boolean isServiceHealthy();
}