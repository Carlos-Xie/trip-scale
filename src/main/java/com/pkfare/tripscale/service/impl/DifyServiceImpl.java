package com.pkfare.tripscale.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pkfare.tripscale.config.DifyServiceConfig;
import com.pkfare.tripscale.dto.DestinationSuggestion;
import com.pkfare.tripscale.dto.GuessMeResponse;
import com.pkfare.tripscale.exception.BusinessException;
import com.pkfare.tripscale.model.Inspirations;
import com.pkfare.tripscale.model.LastVisit;
import com.pkfare.tripscale.model.RecentFocus;
import com.pkfare.tripscale.service.DifyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of DifyService for integrating with Dify AI API
 */
@Service
public class DifyServiceImpl implements DifyService {
    
    private static final Logger logger = LoggerFactory.getLogger(DifyServiceImpl.class);
    
    private final RestTemplate restTemplate;
    private final DifyServiceConfig config;
    private final ObjectMapper objectMapper;
    
    @Autowired
    public DifyServiceImpl(RestTemplate restTemplate, DifyServiceConfig config, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.config = config;
        this.objectMapper = objectMapper;
    }
    
    @Override
    public GuessMeResponse guessDestination(Inspirations inspirations) {
        logger.info("Calling Dify API to guess destination based on inspirations");
        
        if (inspirations == null) {
            throw new BusinessException("Inspirations data is required", "INVALID_INPUT");
        }
        
        try {
            // Prepare request payload
            Map<String, Object> requestPayload = buildDifyRequestPayload(inspirations);
            
            // Make API call with retry logic
            JsonNode difyResponse = callDifyApiWithRetry(requestPayload);
            
            // Process and transform response
            GuessMeResponse response = processDifyResponse(difyResponse);
            
            logger.info("Successfully processed Dify API response with {} suggestions", 
                       response.getSuggestions().size());
            
            return response;
            
        } catch (BusinessException e) {
            logger.error("Business error in Dify service: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error calling Dify API", e);
            throw new BusinessException("Failed to get destination suggestions from AI service", 
                                      "DIFY_SERVICE_ERROR", e);
        }
    }
    
    @Override
    public boolean isServiceHealthy() {
        try {
            String healthUrl = config.getBaseUrl() + "/health";
            HttpHeaders headers = createHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                healthUrl, HttpMethod.GET, entity, String.class);
            
            return response.getStatusCode().is2xxSuccessful();
            
        } catch (Exception e) {
            logger.warn("Dify service health check failed", e);
            return false;
        }
    }
    
    /**
     * Build the request payload for Dify API
     */
    private Map<String, Object> buildDifyRequestPayload(Inspirations inspirations) {
        Map<String, Object> payload = new HashMap<>();
        
        // Add user context
        Map<String, Object> userContext = new HashMap<>();
        userContext.put("age", inspirations.getAge());
        
        // Add travel style preferences
        if (inspirations.getTravelStyle() != null && !inspirations.getTravelStyle().isEmpty()) {
            userContext.put("travel_style", inspirations.getTravelStyle());
        }
        
        // Add recent focus destinations
        if (inspirations.getRecentFocus() != null && !inspirations.getRecentFocus().isEmpty()) {
            List<Map<String, Object>> recentFocusData = inspirations.getRecentFocus().stream()
                .map(focus -> {
                    Map<String, Object> focusMap = new HashMap<>();
                    focusMap.put("destination", focus.getDestination());
                    focusMap.put("priority", focus.getPriority());
                    return focusMap;
                })
                .collect(Collectors.toList());
            userContext.put("recent_focus", recentFocusData);
        }
        
        // Add last 5 years visits
        if (inspirations.getLast5YearVisits() != null && !inspirations.getLast5YearVisits().isEmpty()) {
            List<Map<String, Object>> visitData = inspirations.getLast5YearVisits().stream()
                .map(visit -> {
                    Map<String, Object> visitMap = new HashMap<>();
                    visitMap.put("date", visit.getDate());
                    visitMap.put("locations", visit.getLocations());
                    return visitMap;
                })
                .collect(Collectors.toList());
            userContext.put("past_visits", visitData);
        }
        
        payload.put("user_context", userContext);
        payload.put("request_type", "destination_suggestion");
        payload.put("max_suggestions", 5);
        
        return payload;
    }
    
    /**
     * Call Dify API with retry logic
     */
    private JsonNode callDifyApiWithRetry(Map<String, Object> requestPayload) {
        String apiUrl = config.getBaseUrl() + "/api/v1/chat-messages";
        HttpHeaders headers = createHeaders();
        
        int attempts = 0;
        Exception lastException = null;
        
        while (attempts < config.getMaxRetries()) {
            attempts++;
            
            try {
                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestPayload, headers);
                
                ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl, HttpMethod.POST, entity, String.class);
                
                if (response.getStatusCode().is2xxSuccessful()) {
                    return objectMapper.readTree(response.getBody());
                } else {
                    throw new BusinessException("Dify API returned non-success status: " + 
                                              response.getStatusCode(), "DIFY_API_ERROR");
                }
                
            } catch (HttpClientErrorException e) {
                logger.warn("Dify API client error (attempt {}): {}", attempts, e.getMessage());
                if (e.getStatusCode() == HttpStatus.BAD_REQUEST || 
                    e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                    // Don't retry for client errors
                    throw new BusinessException("Invalid request to Dify API: " + e.getMessage(), 
                                              "DIFY_CLIENT_ERROR", e);
                }
                lastException = e;
                
            } catch (HttpServerErrorException e) {
                logger.warn("Dify API server error (attempt {}): {}", attempts, e.getMessage());
                lastException = e;
                
            } catch (ResourceAccessException e) {
                logger.warn("Dify API connection error (attempt {}): {}", attempts, e.getMessage());
                lastException = e;
                
            } catch (Exception e) {
                logger.warn("Unexpected error calling Dify API (attempt {}): {}", attempts, e.getMessage());
                lastException = e;
            }
            
            // Wait before retry (except for last attempt)
            if (attempts < config.getMaxRetries()) {
                try {
                    Thread.sleep(config.getRetryDelayMs() * attempts); // Exponential backoff
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new BusinessException("Interrupted while retrying Dify API call", 
                                              "DIFY_RETRY_INTERRUPTED", ie);
                }
            }
        }
        
        // All retries failed
        throw new BusinessException("Failed to call Dify API after " + config.getMaxRetries() + 
                                  " attempts", "DIFY_SERVICE_UNAVAILABLE", lastException);
    }
    
    /**
     * Process Dify API response and transform to GuessMeResponse
     */
    private GuessMeResponse processDifyResponse(JsonNode difyResponse) {
        try {
            GuessMeResponse response = new GuessMeResponse();
            response.setSessionId(UUID.randomUUID().toString());
            
            // Extract message from Dify response
            String message = "AI-generated destination suggestions based on your preferences";
            if (difyResponse.has("answer")) {
                JsonNode answerNode = difyResponse.get("answer");
                if (answerNode.isTextual()) {
                    message = answerNode.asText();
                }
            }
            response.setMessage(message);
            
            // Parse destination suggestions from response
            List<DestinationSuggestion> suggestions = extractDestinationSuggestions(difyResponse);
            response.setSuggestions(suggestions);
            
            return response;
            
        } catch (Exception e) {
            logger.error("Error processing Dify API response", e);
            throw new BusinessException("Failed to process AI response", "DIFY_RESPONSE_PROCESSING_ERROR", e);
        }
    }
    
    /**
     * Extract destination suggestions from Dify response
     */
    private List<DestinationSuggestion> extractDestinationSuggestions(JsonNode difyResponse) {
        List<DestinationSuggestion> suggestions = new ArrayList<>();
        
        try {
            // Try to parse structured suggestions if available
            if (difyResponse.has("suggestions")) {
                JsonNode suggestionsNode = difyResponse.get("suggestions");
                if (suggestionsNode.isArray()) {
                    for (JsonNode suggestionNode : suggestionsNode) {
                        DestinationSuggestion suggestion = parseSuggestionNode(suggestionNode);
                        if (suggestion != null) {
                            suggestions.add(suggestion);
                        }
                    }
                }
            }
            
            // If no structured suggestions found, try to parse from answer text
            if (suggestions.isEmpty() && difyResponse.has("answer")) {
                suggestions = parseDestinationsFromText(difyResponse.get("answer").asText());
            }
            
            // Ensure we have at least some suggestions (fallback)
            if (suggestions.isEmpty()) {
                suggestions = createFallbackSuggestions();
            }
            
        } catch (Exception e) {
            logger.warn("Error extracting suggestions from Dify response, using fallback", e);
            suggestions = createFallbackSuggestions();
        }
        
        return suggestions;
    }
    
    /**
     * Parse individual suggestion node from Dify response
     */
    private DestinationSuggestion parseSuggestionNode(JsonNode suggestionNode) {
        try {
            DestinationSuggestion suggestion = new DestinationSuggestion();
            
            if (suggestionNode.has("destination")) {
                suggestion.setDestination(suggestionNode.get("destination").asText());
            }
            
            if (suggestionNode.has("reason")) {
                suggestion.setReason(suggestionNode.get("reason").asText());
            }
            
            if (suggestionNode.has("confidence")) {
                suggestion.setConfidence(suggestionNode.get("confidence").asDouble());
            } else {
                suggestion.setConfidence(0.7); // Default confidence
            }
            
            return suggestion.getDestination() != null ? suggestion : null;
            
        } catch (Exception e) {
            logger.warn("Error parsing suggestion node", e);
            return null;
        }
    }
    
    /**
     * Parse destinations from free text response
     */
    private List<DestinationSuggestion> parseDestinationsFromText(String text) {
        List<DestinationSuggestion> suggestions = new ArrayList<>();
        
        // Simple text parsing logic - this could be enhanced with NLP
        String[] lines = text.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.length() > 0 && (line.contains(",") || line.toLowerCase().contains("suggest"))) {
                DestinationSuggestion suggestion = new DestinationSuggestion();
                suggestion.setDestination(extractDestinationFromLine(line));
                suggestion.setReason("AI recommendation based on your preferences");
                suggestion.setConfidence(0.6);
                
                if (suggestion.getDestination() != null) {
                    suggestions.add(suggestion);
                }
            }
        }
        
        return suggestions;
    }
    
    /**
     * Extract destination name from a text line
     */
    private String extractDestinationFromLine(String line) {
        // Simple extraction logic - look for patterns like "City, Country"
        if (line.contains(",")) {
            String[] parts = line.split(",");
            if (parts.length >= 2) {
                return (parts[0].trim() + ", " + parts[1].trim()).replaceAll("[^a-zA-Z0-9,\\s]", "");
            }
        }
        return null;
    }
    
    /**
     * Create fallback suggestions when parsing fails
     */
    private List<DestinationSuggestion> createFallbackSuggestions() {
        List<DestinationSuggestion> fallback = new ArrayList<>();
        
        DestinationSuggestion suggestion1 = new DestinationSuggestion();
        suggestion1.setDestination("Paris, France");
        suggestion1.setReason("Popular destination with rich culture and history");
        suggestion1.setConfidence(0.5);
        fallback.add(suggestion1);
        
        DestinationSuggestion suggestion2 = new DestinationSuggestion();
        suggestion2.setDestination("Tokyo, Japan");
        suggestion2.setReason("Modern city with unique cultural experiences");
        suggestion2.setConfidence(0.5);
        fallback.add(suggestion2);
        
        return fallback;
    }
    
    /**
     * Create HTTP headers for Dify API calls
     */
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + config.getApiKey());
        headers.set("User-Agent", "TripScale-Backend/1.0");
        return headers;
    }
}