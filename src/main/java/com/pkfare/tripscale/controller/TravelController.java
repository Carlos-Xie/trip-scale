package com.pkfare.tripscale.controller;

import com.pkfare.tripscale.dto.DestinationConfirmationRequest;
import com.pkfare.tripscale.dto.GuessMeRequest;
import com.pkfare.tripscale.dto.GuessMeResponse;
import com.pkfare.tripscale.dto.TravelDemandRequest;
import com.pkfare.tripscale.dto.TravelDetailsRequest;
import com.pkfare.tripscale.dto.TripRoutesResponse;
import com.pkfare.tripscale.service.TravelService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for travel route guidance endpoints.
 * Handles both direct destination input and AI-guided discovery workflows.
 */
@RestController
@RequestMapping("/api/travel")
@CrossOrigin(origins = "*")
public class TravelController {
    
    private static final Logger logger = LoggerFactory.getLogger(TravelController.class);
    
    private final TravelService travelService;
    
    @Autowired
    public TravelController(TravelService travelService) {
        this.travelService = travelService;
    }
    
    /**
     * Direct input endpoint for users who know their travel destination
     * 
     * @param request Travel demand request with destinations, days, passengers, and budget
     * @return TripRoutesResponse containing suitable routes and applied preferences
     */
    @PostMapping("/direct-input")
    public ResponseEntity<TripRoutesResponse> directInput(@Valid @RequestBody TravelDemandRequest request) {
        logger.info("Processing direct input request for user: {}", request.getUserId());
        
        try {
            TripRoutesResponse response = travelService.processDirectInput(request);
            logger.info("Successfully processed direct input for user: {}, found {} routes", 
                       request.getUserId(), response.getRoutes().size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error processing direct input for user: {}", request.getUserId(), e);
            throw e;
        }
    }
    
    /**
     * GuessMe endpoint to initiate AI-guided destination discovery
     * 
     * @param request GuessMe request containing user ID
     * @return GuessMeResponse with destination suggestions and session ID
     */
    @PostMapping("/guess-me")
    public ResponseEntity<GuessMeResponse> guessMe(@Valid @RequestBody GuessMeRequest request) {
        logger.info("Initiating GuessMe for user: {}", request.getUserId());
        
        try {
            GuessMeResponse response = travelService.initiateGuessMe(request);
            logger.info("Successfully initiated GuessMe for user: {}, session: {}, suggestions: {}", 
                       request.getUserId(), response.getSessionId(), response.getSuggestions().size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error initiating GuessMe for user: {}", request.getUserId(), e);
            throw e;
        }
    }
    
    /**
     * Confirm destination endpoint for AI-guided discovery workflow
     * 
     * @param request Destination confirmation with selected destination and session ID
     * @return Response with session ID for collecting remaining travel details
     */
    @PostMapping("/confirm-destination")
    public ResponseEntity<Map<String, String>> confirmDestination(@Valid @RequestBody DestinationConfirmationRequest request) {
        logger.info("Processing destination confirmation for session: {}, destination: {}, confirmed: {}", 
                   request.getSessionId(), request.getDestination(), request.isConfirmed());
        
        try {
            String sessionId = travelService.confirmDestination(request);
            
            Map<String, String> response = new HashMap<>();
            response.put("sessionId", sessionId);
            response.put("status", request.isConfirmed() ? "confirmed" : "rejected");
            response.put("message", request.isConfirmed() ? 
                "Destination confirmed. Please provide travel details." : 
                "Destination rejected. Please try again.");
            
            logger.info("Successfully processed destination confirmation for session: {}", sessionId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error confirming destination for session: {}", request.getSessionId(), e);
            throw e;
        }
    }
    
    /**
     * Collect travel details endpoint for completing AI-guided discovery
     * 
     * @param request Travel details including days, passengers, and budget
     * @return TripRoutesResponse containing suitable routes for confirmed destination
     */
    @PostMapping("/collect-details")
    public ResponseEntity<TripRoutesResponse> collectDetails(@Valid @RequestBody TravelDetailsRequest request) {
        logger.info("Collecting travel details for session: {}", request.getSessionId());
        
        try {
            TripRoutesResponse response = travelService.collectTravelDetails(request);
            logger.info("Successfully collected travel details for session: {}, found {} routes", 
                       request.getSessionId(), response.getRoutes().size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error collecting travel details for session: {}", request.getSessionId(), e);
            throw e;
        }
    }
    
    /**
     * Get trip routes endpoint for retrieving routes by session ID
     * 
     * @param sessionId The session identifier
     * @return TripRoutesResponse containing routes for the session
     */
    @GetMapping("/routes/{sessionId}")
    public ResponseEntity<TripRoutesResponse> getTripRoutes(@PathVariable String sessionId) {
        logger.info("Retrieving trip routes for session: {}", sessionId);
        
        try {
            TripRoutesResponse response = travelService.getTripRoutes(sessionId);
            logger.info("Successfully retrieved trip routes for session: {}, found {} routes", 
                       sessionId, response.getRoutes().size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error retrieving trip routes for session: {}", sessionId, e);
            throw e;
        }
    }
}