package com.pkfare.tripscale.service;

import com.pkfare.tripscale.dto.DestinationConfirmationRequest;
import com.pkfare.tripscale.dto.GuessMeRequest;
import com.pkfare.tripscale.dto.GuessMeResponse;
import com.pkfare.tripscale.dto.TravelDemandRequest;
import com.pkfare.tripscale.dto.TravelDetailsRequest;
import com.pkfare.tripscale.dto.TripRoutesResponse;

/**
 * Service interface for orchestrating travel route determination workflows.
 * Handles both direct destination input and AI-guided discovery processes.
 */
public interface TravelService {
    
    /**
     * Process direct travel input where user knows their destination
     * 
     * @param request Travel demand request with destinations, days, passengers, and budget
     * @return TripRoutesResponse containing suitable routes and applied preferences
     * @throws com.pkfare.tripscale.exception.BusinessException if processing fails
     * @throws com.pkfare.tripscale.exception.ValidationException if request validation fails
     */
    TripRoutesResponse processDirectInput(TravelDemandRequest request);
    
    /**
     * Initiate AI-guided discovery process for users who need destination inspiration
     * 
     * @param request GuessMe request containing user ID
     * @return GuessMeResponse with destination suggestions and session ID
     * @throws com.pkfare.tripscale.exception.ResourceNotFoundException if user not found
     * @throws com.pkfare.tripscale.exception.BusinessException if AI service fails
     */
    GuessMeResponse initiateGuessMe(GuessMeRequest request);
    
    /**
     * Process destination confirmation from AI-guided discovery
     * 
     * @param request Destination confirmation with selected destination and session ID
     * @return Session ID for collecting remaining travel details
     * @throws com.pkfare.tripscale.exception.ValidationException if session or destination invalid
     */
    String confirmDestination(DestinationConfirmationRequest request);
    
    /**
     * Collect remaining travel details after destination confirmation
     * 
     * @param request Travel details including days, passengers, and budget
     * @return TripRoutesResponse containing suitable routes for confirmed destination
     * @throws com.pkfare.tripscale.exception.ValidationException if session invalid or details incomplete
     * @throws com.pkfare.tripscale.exception.BusinessException if route search fails
     */
    TripRoutesResponse collectTravelDetails(TravelDetailsRequest request);
    
    /**
     * Retrieve trip routes for an existing session
     * 
     * @param sessionId The session identifier
     * @return TripRoutesResponse containing routes for the session
     * @throws com.pkfare.tripscale.exception.ResourceNotFoundException if session not found
     */
    TripRoutesResponse getTripRoutes(String sessionId);
}