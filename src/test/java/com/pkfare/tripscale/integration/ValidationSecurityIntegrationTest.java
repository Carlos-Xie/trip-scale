package com.pkfare.tripscale.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pkfare.tripscale.dto.TravelDemandRequest;
import com.pkfare.tripscale.dto.GuessMeRequest;
import com.pkfare.tripscale.dto.DestinationConfirmationRequest;
import com.pkfare.tripscale.dto.TravelDetailsRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.greaterThan;

/**
 * Integration tests for validation and security measures
 */
@WebMvcTest
class ValidationSecurityIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    @DisplayName("Should reject request with invalid destinations")
    void shouldRejectRequestWithInvalidDestinations() throws Exception {
        TravelDemandRequest request = new TravelDemandRequest(
            Arrays.asList("Paris<script>alert('xss')</script>"),
            7,
            2,
            "adult",
            "medium",
            "user123"
        );
        
        mockMvc.perform(post("/api/travel/direct-input")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.message").value("Input validation failed"));
    }
    
    @Test
    @DisplayName("Should reject request with empty destinations")
    void shouldRejectRequestWithEmptyDestinations() throws Exception {
        TravelDemandRequest request = new TravelDemandRequest(
            Collections.emptyList(),
            7,
            2,
            "adult",
            "medium",
            "user123"
        );
        
        mockMvc.perform(post("/api/travel/direct-input")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.details").isArray());
    }
    
    @Test
    @DisplayName("Should reject request with too many destinations")
    void shouldRejectRequestWithTooManyDestinations() throws Exception {
        TravelDemandRequest request = new TravelDemandRequest(
            Arrays.asList("Paris", "London", "Tokyo", "New York", "Sydney", 
                         "Berlin", "Rome", "Madrid", "Amsterdam", "Vienna", "Prague"),
            7,
            2,
            "adult",
            "medium",
            "user123"
        );
        
        mockMvc.perform(post("/api/travel/direct-input")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }
    
    @Test
    @DisplayName("Should reject request with invalid days")
    void shouldRejectRequestWithInvalidDays() throws Exception {
        TravelDemandRequest request = new TravelDemandRequest(
            Arrays.asList("Paris"),
            0, // Invalid days
            2,
            "adult",
            "medium",
            "user123"
        );
        
        mockMvc.perform(post("/api/travel/direct-input")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }
    
    @Test
    @DisplayName("Should reject request with too many days")
    void shouldRejectRequestWithTooManyDays() throws Exception {
        TravelDemandRequest request = new TravelDemandRequest(
            Arrays.asList("Paris"),
            400, // Too many days
            2,
            "adult",
            "medium",
            "user123"
        );
        
        mockMvc.perform(post("/api/travel/direct-input")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }
    
    @Test
    @DisplayName("Should reject request with invalid passenger count")
    void shouldRejectRequestWithInvalidPassengerCount() throws Exception {
        TravelDemandRequest request = new TravelDemandRequest(
            Arrays.asList("Paris"),
            7,
            0, // Invalid passenger count
            "adult",
            "medium",
            "user123"
        );
        
        mockMvc.perform(post("/api/travel/direct-input")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }
    
    @Test
    @DisplayName("Should reject request with too many passengers")
    void shouldRejectRequestWithTooManyPassengers() throws Exception {
        TravelDemandRequest request = new TravelDemandRequest(
            Arrays.asList("Paris"),
            7,
            100, // Too many passengers
            "adult",
            "medium",
            "user123"
        );
        
        mockMvc.perform(post("/api/travel/direct-input")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }
    
    @Test
    @DisplayName("Should reject request with invalid passenger type")
    void shouldRejectRequestWithInvalidPassengerType() throws Exception {
        TravelDemandRequest request = new TravelDemandRequest(
            Arrays.asList("Paris"),
            7,
            2,
            "invalid-type", // Invalid passenger type
            "medium",
            "user123"
        );
        
        mockMvc.perform(post("/api/travel/direct-input")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }
    
    @Test
    @DisplayName("Should reject request with invalid budget")
    void shouldRejectRequestWithInvalidBudget() throws Exception {
        TravelDemandRequest request = new TravelDemandRequest(
            Arrays.asList("Paris"),
            7,
            2,
            "adult",
            "invalid-budget", // Invalid budget
            "user123"
        );
        
        mockMvc.perform(post("/api/travel/direct-input")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }
    
    @Test
    @DisplayName("Should reject request with invalid user ID")
    void shouldRejectRequestWithInvalidUserId() throws Exception {
        TravelDemandRequest request = new TravelDemandRequest(
            Arrays.asList("Paris"),
            7,
            2,
            "adult",
            "medium",
            "user@invalid" // Invalid user ID
        );
        
        mockMvc.perform(post("/api/travel/direct-input")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }
    
    @Test
    @DisplayName("Should reject GuessMe request with invalid user ID")
    void shouldRejectGuessMeRequestWithInvalidUserId() throws Exception {
        GuessMeRequest request = new GuessMeRequest("user@invalid");
        
        mockMvc.perform(post("/api/travel/guess-me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }
    
    @Test
    @DisplayName("Should reject destination confirmation with invalid session ID")
    void shouldRejectDestinationConfirmationWithInvalidSessionId() throws Exception {
        DestinationConfirmationRequest request = new DestinationConfirmationRequest(
            "session@invalid",
            "Paris",
            true
        );
        
        mockMvc.perform(post("/api/travel/confirm-destination")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }
    
    @Test
    @DisplayName("Should reject destination confirmation with invalid destination")
    void shouldRejectDestinationConfirmationWithInvalidDestination() throws Exception {
        DestinationConfirmationRequest request = new DestinationConfirmationRequest(
            "session123",
            "Paris<script>alert('xss')</script>",
            true
        );
        
        mockMvc.perform(post("/api/travel/confirm-destination")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }
    
    @Test
    @DisplayName("Should reject travel details with invalid session ID")
    void shouldRejectTravelDetailsWithInvalidSessionId() throws Exception {
        TravelDetailsRequest request = new TravelDetailsRequest(
            "session@invalid",
            7,
            2,
            "adult",
            "medium"
        );
        
        mockMvc.perform(post("/api/travel/collect-details")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }
    
    @Test
    @DisplayName("Should include request ID in response headers")
    void shouldIncludeRequestIdInResponseHeaders() throws Exception {
        TravelDemandRequest request = new TravelDemandRequest(
            Arrays.asList("Paris"),
            7,
            2,
            "adult",
            "medium",
            "user@invalid" // This will cause validation error
        );
        
        mockMvc.perform(post("/api/travel/direct-input")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(header().exists("X-Request-ID"));
    }
    
    @Test
    @DisplayName("Should handle multiple validation errors")
    void shouldHandleMultipleValidationErrors() throws Exception {
        TravelDemandRequest request = new TravelDemandRequest(
            Collections.emptyList(), // Invalid destinations
            0, // Invalid days
            0, // Invalid passenger count
            "invalid-type", // Invalid passenger type
            "invalid-budget", // Invalid budget
            "user@invalid" // Invalid user ID
        );
        
        mockMvc.perform(post("/api/travel/direct-input")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details.length()").value(greaterThan(1)));
    }
}