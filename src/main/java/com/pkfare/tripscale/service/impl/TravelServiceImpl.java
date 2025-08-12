package com.pkfare.tripscale.service.impl;

import com.pkfare.tripscale.dto.DestinationConfirmationRequest;
import com.pkfare.tripscale.dto.GuessMeRequest;
import com.pkfare.tripscale.dto.GuessMeResponse;
import com.pkfare.tripscale.dto.TravelDemandRequest;
import com.pkfare.tripscale.dto.TravelDetailsRequest;
import com.pkfare.tripscale.dto.TripRoutesResponse;
import com.pkfare.tripscale.exception.BusinessException;
import com.pkfare.tripscale.exception.ExternalServiceException;
import com.pkfare.tripscale.exception.NoRoutesFoundException;
import com.pkfare.tripscale.exception.ResourceNotFoundException;
import com.pkfare.tripscale.exception.TravelServiceException;
import com.pkfare.tripscale.exception.UserNotFoundException;
import com.pkfare.tripscale.exception.ValidationException;
import com.pkfare.tripscale.model.Inspirations;
import com.pkfare.tripscale.model.PersonalPreferences;
import com.pkfare.tripscale.model.TravelDemand;
import com.pkfare.tripscale.model.TripRoute;
import com.pkfare.tripscale.service.DifyService;
import com.pkfare.tripscale.service.MemoryService;
import com.pkfare.tripscale.service.SessionManager;
import com.pkfare.tripscale.service.TravelService;
import com.pkfare.tripscale.service.TripKnowledgeService;
import com.pkfare.tripscale.util.InputSanitizer;
import com.pkfare.tripscale.util.RateLimiter;
import com.pkfare.tripscale.exception.RateLimitExceededException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of TravelService for orchestrating travel route determination workflows
 */
@Service
public class TravelServiceImpl implements TravelService {
    
    private static final Logger logger = LoggerFactory.getLogger(TravelServiceImpl.class);
    
    private final DifyService difyService;
    private final MemoryService memoryService;
    private final TripKnowledgeService tripKnowledgeService;
    private final InputSanitizer inputSanitizer;
    private final RateLimiter rateLimiter;
    
    // In-memory session storage (in production, this would be Redis or database)
    private final ConcurrentHashMap<String, SessionManager.SessionData> sessionStore = new ConcurrentHashMap<>();
    
    @Autowired
    public TravelServiceImpl(DifyService difyService, 
                           MemoryService memoryService,
                           TripKnowledgeService tripKnowledgeService,
                           InputSanitizer inputSanitizer,
                           RateLimiter rateLimiter) {
        this.difyService = difyService;
        this.memoryService = memoryService;
        this.tripKnowledgeService = tripKnowledgeService;
        this.inputSanitizer = inputSanitizer;
        this.rateLimiter = rateLimiter;
    }
    
    @Override
    public TripRoutesResponse processDirectInput(TravelDemandRequest request) {
        logger.info("Processing direct input for user: {}", request.getUserId());
        
        try {
            // Sanitize input data
            sanitizeTravelDemandRequest(request);
            
            // Check rate limits for external service calls
            checkRateLimits(request.getUserId());
            
            // Create travel demand from request
            TravelDemand travelDemand = createTravelDemandFromRequest(request);
            
            // Retrieve personal preferences
            PersonalPreferences personalPreferences = memoryService.getPersonalPreferences(request.getUserId());
            
            // Find suitable routes
            List<TripRoute> routes = tripKnowledgeService.findSuitableRoutes(travelDemand, personalPreferences);
            
            // Update user history
            memoryService.updateUserHistory(request.getUserId(), travelDemand);
            
            // Create session for tracking
            String sessionId = SessionManager.generateSessionId();
            SessionManager.SessionData sessionData = new SessionManager.SessionData(sessionId, request.getUserId());
            sessionData.setTravelDemand(travelDemand);
            sessionData.setPersonalPreferences(personalPreferences);
            sessionData.setTripRoutes(routes);
            sessionData.setStatus(SessionManager.SessionStatus.ROUTES_FOUND);
            sessionStore.put(sessionId, sessionData);
            
            logger.info("Found {} routes for direct input request", routes.size());
            
            return new TripRoutesResponse(sessionId, routes, personalPreferences, "success");
            
        } catch (UserNotFoundException e) {
            logger.error("User not found for direct input: {}", request.getUserId(), e);
            throw e;
        } catch (ExternalServiceException e) {
            logger.error("External service error during direct input: {}", e.getMessage(), e);
            throw e;
        } catch (ValidationException e) {
            logger.warn("Validation error during direct input: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error processing direct input for user: {}", request.getUserId(), e);
            throw new TravelServiceException("Failed to process direct travel input", e);
        }
    }
    
    @Override
    public GuessMeResponse initiateGuessMe(GuessMeRequest request) {
        logger.info("Initiating GuessMe for user: {}", request.getUserId());
        
        try {
            // Sanitize input data
            sanitizeGuessMeRequest(request);
            
            // Check rate limits for external service calls
            checkRateLimits(request.getUserId());
            
            // Retrieve user inspirations
            Inspirations inspirations = memoryService.getInspirations(request.getUserId());
            
            // Get AI destination suggestions
            GuessMeResponse aiResponse = difyService.guessDestination(inspirations);
            
            // Create session for tracking
            String sessionId = SessionManager.generateSessionId();
            SessionManager.SessionData sessionData = new SessionManager.SessionData(sessionId, request.getUserId());
            sessionData.setStatus(SessionManager.SessionStatus.INITIATED);
            sessionStore.put(sessionId, sessionData);
            
            // Update response with session ID
            aiResponse.setSessionId(sessionId);
            
            logger.info("Generated {} destination suggestions for user: {}", 
                       aiResponse.getSuggestions().size(), request.getUserId());
            
            return aiResponse;
            
        } catch (UserNotFoundException e) {
            logger.error("User not found for GuessMe: {}", request.getUserId(), e);
            throw e;
        } catch (ExternalServiceException e) {
            logger.error("External service error during GuessMe: {}", e.getMessage(), e);
            throw e;
        } catch (ValidationException e) {
            logger.warn("Validation error during GuessMe: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error initiating GuessMe for user: {}", request.getUserId(), e);
            throw new TravelServiceException("Failed to initiate GuessMe process", e);
        }
    }
    
    @Override
    public String confirmDestination(DestinationConfirmationRequest request) {
        logger.info("Confirming destination for session: {}", request.getSessionId());
        
        // Sanitize input data
        sanitizeDestinationConfirmationRequest(request);
        
        SessionManager.SessionData sessionData = getSessionData(request.getSessionId());
        
        if (sessionData.getStatus() != SessionManager.SessionStatus.INITIATED) {
            throw new ValidationException("Invalid session status for destination confirmation");
        }
        
        // Update session with confirmed destination
        sessionData.setSelectedDestination(request.getDestination());
        sessionData.setStatus(SessionManager.SessionStatus.DESTINATION_CONFIRMED);
        sessionData.updateLastAccessed();
        
        logger.info("Destination confirmed: {} for session: {}", 
                   request.getDestination(), request.getSessionId());
        
        return request.getSessionId();
    }
    
    @Override
    public TripRoutesResponse collectTravelDetails(TravelDetailsRequest request) {
        logger.info("Collecting travel details for session: {}", request.getSessionId());
        
        // Sanitize input data
        sanitizeTravelDetailsRequest(request);
        
        SessionManager.SessionData sessionData = getSessionData(request.getSessionId());
        
        if (sessionData.getStatus() != SessionManager.SessionStatus.DESTINATION_CONFIRMED) {
            throw new ValidationException("Invalid session status for travel details collection");
        }
        
        try {
            // Create travel demand with confirmed destination and provided details
            TravelDemand travelDemand = new TravelDemand();
            travelDemand.setMustGoDestinations(List.of(sessionData.getSelectedDestination()));
            travelDemand.setDays(request.getDays());
            travelDemand.setPassenger(request.getPassenger());
            travelDemand.setPassengerType(request.getPassengerType());
            travelDemand.setBudgets(request.getBudgets());
            travelDemand.setSessionId(request.getSessionId());
            
            // Retrieve personal preferences
            PersonalPreferences personalPreferences = memoryService.getPersonalPreferences(sessionData.getUserId());
            
            // Find suitable routes
            List<TripRoute> routes = tripKnowledgeService.findSuitableRoutes(travelDemand, personalPreferences);
            
            // Update user history
            memoryService.updateUserHistory(sessionData.getUserId(), travelDemand);
            
            // Update session
            sessionData.setTravelDemand(travelDemand);
            sessionData.setPersonalPreferences(personalPreferences);
            sessionData.setTripRoutes(routes);
            sessionData.setStatus(SessionManager.SessionStatus.ROUTES_FOUND);
            sessionData.updateLastAccessed();
            
            logger.info("Found {} routes for GuessMe session: {}", routes.size(), request.getSessionId());
            
            return new TripRoutesResponse(request.getSessionId(), routes, personalPreferences, "success");
            
        } catch (ExternalServiceException e) {
            logger.error("External service error during travel details collection: {}", e.getMessage(), e);
            throw e;
        } catch (ValidationException e) {
            logger.warn("Validation error during travel details collection: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error collecting travel details for session: {}", request.getSessionId(), e);
            throw new TravelServiceException("Failed to collect travel details", e);
        }
    }
    
    @Override
    public TripRoutesResponse getTripRoutes(String sessionId) {
        logger.info("Retrieving trip routes for session: {}", sessionId);
        
        // Sanitize session ID
        String sanitizedSessionId = inputSanitizer.sanitizeSessionId(sessionId);
        
        SessionManager.SessionData sessionData = getSessionData(sanitizedSessionId);
        
        if (sessionData.getStatus() != SessionManager.SessionStatus.ROUTES_FOUND) {
            throw new ValidationException("No routes available for session: " + sessionId);
        }
        
        sessionData.updateLastAccessed();
        
        return new TripRoutesResponse(
            sessionId,
            sessionData.getTripRoutes(),
            sessionData.getPersonalPreferences(),
            "success"
        );
    }
    
    /**
     * Helper method to retrieve session data with validation
     */
    private SessionManager.SessionData getSessionData(String sessionId) {
        if (sessionId == null || sessionId.trim().isEmpty()) {
            throw new ValidationException("Session ID is required");
        }
        
        SessionManager.SessionData sessionData = sessionStore.get(sessionId);
        if (sessionData == null) {
            throw new ResourceNotFoundException("Session not found: " + sessionId);
        }
        
        return sessionData;
    }
    
    /**
     * Helper method to create TravelDemand from TravelDemandRequest
     */
    private TravelDemand createTravelDemandFromRequest(TravelDemandRequest request) {
        TravelDemand travelDemand = new TravelDemand();
        travelDemand.setMustGoDestinations(request.getMustGoDestinations());
        travelDemand.setDays(request.getDays());
        travelDemand.setPassenger(request.getPassenger());
        travelDemand.setPassengerType(request.getPassengerType());
        travelDemand.setBudgets(request.getBudgets());
        travelDemand.setSessionId(SessionManager.generateSessionId());
        return travelDemand;
    }
    
    /**
     * Sanitize TravelDemandRequest input
     */
    private void sanitizeTravelDemandRequest(TravelDemandRequest request) {
        try {
            // Sanitize user ID
            request.setUserId(inputSanitizer.sanitizeUserId(request.getUserId()));
            
            // Sanitize destinations
            if (request.getMustGoDestinations() != null) {
                request.setMustGoDestinations(inputSanitizer.sanitizeDestinations(request.getMustGoDestinations()));
            }
            
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid input data: " + e.getMessage());
        }
    }
    
    /**
     * Sanitize GuessMeRequest input
     */
    private void sanitizeGuessMeRequest(GuessMeRequest request) {
        try {
            // Sanitize user ID
            request.setUserId(inputSanitizer.sanitizeUserId(request.getUserId()));
            
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid input data: " + e.getMessage());
        }
    }
    
    /**
     * Sanitize DestinationConfirmationRequest input
     */
    private void sanitizeDestinationConfirmationRequest(DestinationConfirmationRequest request) {
        try {
            // Sanitize session ID
            request.setSessionId(inputSanitizer.sanitizeSessionId(request.getSessionId()));
            
            // Sanitize destination
            request.setDestination(inputSanitizer.sanitizeDestination(request.getDestination()));
            
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid input data: " + e.getMessage());
        }
    }
    
    /**
     * Sanitize TravelDetailsRequest input
     */
    private void sanitizeTravelDetailsRequest(TravelDetailsRequest request) {
        try {
            // Sanitize session ID
            request.setSessionId(inputSanitizer.sanitizeSessionId(request.getSessionId()));
            
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid input data: " + e.getMessage());
        }
    }
    
    /**
     * Check rate limits for external service calls
     */
    private void checkRateLimits(String userId) {
        // Check Memory service rate limit
        if (!rateLimiter.isRequestAllowed("memory", userId)) {
            long retryAfter = rateLimiter.getSecondsUntilReset("memory", userId);
            throw new RateLimitExceededException("memory", userId, retryAfter);
        }
        
        // Check Dify service rate limit
        if (!rateLimiter.isRequestAllowed("dify", userId)) {
            long retryAfter = rateLimiter.getSecondsUntilReset("dify", userId);
            throw new RateLimitExceededException("dify", userId, retryAfter);
        }
        
        // Check Trip Knowledge service rate limit
        if (!rateLimiter.isRequestAllowed("trip-knowledge", userId)) {
            long retryAfter = rateLimiter.getSecondsUntilReset("trip-knowledge", userId);
            throw new RateLimitExceededException("trip-knowledge", userId, retryAfter);
        }
    }
}