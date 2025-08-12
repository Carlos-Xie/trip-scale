package com.pkfare.tripscale.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DTO validation annotations
 */
class ValidationTest {
    
    private Validator validator;
    
    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    
    @Test
    @DisplayName("Should validate valid TravelDemandRequest")
    void shouldValidateValidTravelDemandRequest() {
        TravelDemandRequest request = new TravelDemandRequest(
            Arrays.asList("Paris", "London"),
            7,
            2,
            "adult",
            "medium",
            "user123"
        );
        
        Set<ConstraintViolation<TravelDemandRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }
    
    @Test
    @DisplayName("Should reject TravelDemandRequest with invalid data")
    void shouldRejectTravelDemandRequestWithInvalidData() {
        TravelDemandRequest request = new TravelDemandRequest(
            Collections.emptyList(), // Empty destinations
            0, // Invalid days
            0, // Invalid passenger count
            "invalid-type", // Invalid passenger type
            "invalid-budget", // Invalid budget
            "user@invalid" // Invalid user ID
        );
        
        Set<ConstraintViolation<TravelDemandRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        
        // Should have violations for all invalid fields
        assertTrue(violations.size() >= 6);
    }
    
    @Test
    @DisplayName("Should reject TravelDemandRequest with too many destinations")
    void shouldRejectTravelDemandRequestWithTooManyDestinations() {
        TravelDemandRequest request = new TravelDemandRequest(
            Arrays.asList("Paris", "London", "Tokyo", "New York", "Sydney", 
                         "Berlin", "Rome", "Madrid", "Amsterdam", "Vienna", "Prague"), // 11 destinations
            7,
            2,
            "adult",
            "medium",
            "user123"
        );
        
        Set<ConstraintViolation<TravelDemandRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        
        boolean hasDestinationSizeViolation = violations.stream()
            .anyMatch(v -> v.getMessage().contains("Must have between 1 and 10 destinations"));
        assertTrue(hasDestinationSizeViolation);
    }
    
    @Test
    @DisplayName("Should reject TravelDemandRequest with too many days")
    void shouldRejectTravelDemandRequestWithTooManyDays() {
        TravelDemandRequest request = new TravelDemandRequest(
            Arrays.asList("Paris"),
            400, // Too many days
            2,
            "adult",
            "medium",
            "user123"
        );
        
        Set<ConstraintViolation<TravelDemandRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        
        boolean hasDaysViolation = violations.stream()
            .anyMatch(v -> v.getMessage().contains("Days cannot exceed 365"));
        assertTrue(hasDaysViolation);
    }
    
    @Test
    @DisplayName("Should validate valid GuessMeRequest")
    void shouldValidateValidGuessMeRequest() {
        GuessMeRequest request = new GuessMeRequest("user123");
        
        Set<ConstraintViolation<GuessMeRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }
    
    @Test
    @DisplayName("Should reject GuessMeRequest with invalid user ID")
    void shouldRejectGuessMeRequestWithInvalidUserId() {
        GuessMeRequest request = new GuessMeRequest("user@invalid");
        
        Set<ConstraintViolation<GuessMeRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        
        boolean hasUserIdViolation = violations.stream()
            .anyMatch(v -> v.getMessage().contains("User ID contains invalid characters"));
        assertTrue(hasUserIdViolation);
    }
    
    @Test
    @DisplayName("Should validate valid TravelDetailsRequest")
    void shouldValidateValidTravelDetailsRequest() {
        TravelDetailsRequest request = new TravelDetailsRequest(
            "session123",
            7,
            2,
            "adult",
            "medium"
        );
        
        Set<ConstraintViolation<TravelDetailsRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }
    
    @Test
    @DisplayName("Should reject TravelDetailsRequest with invalid session ID")
    void shouldRejectTravelDetailsRequestWithInvalidSessionId() {
        TravelDetailsRequest request = new TravelDetailsRequest(
            "session@invalid",
            7,
            2,
            "adult",
            "medium"
        );
        
        Set<ConstraintViolation<TravelDetailsRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        
        boolean hasSessionIdViolation = violations.stream()
            .anyMatch(v -> v.getMessage().contains("Session ID contains invalid characters"));
        assertTrue(hasSessionIdViolation);
    }
    
    @Test
    @DisplayName("Should reject TravelDetailsRequest with too many passengers")
    void shouldRejectTravelDetailsRequestWithTooManyPassengers() {
        TravelDetailsRequest request = new TravelDetailsRequest(
            "session123",
            7,
            100, // Too many passengers
            "adult",
            "medium"
        );
        
        Set<ConstraintViolation<TravelDetailsRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        
        boolean hasPassengerViolation = violations.stream()
            .anyMatch(v -> v.getMessage().contains("Passenger count cannot exceed 50"));
        assertTrue(hasPassengerViolation);
    }
    
    @Test
    @DisplayName("Should validate valid DestinationConfirmationRequest")
    void shouldValidateValidDestinationConfirmationRequest() {
        DestinationConfirmationRequest request = new DestinationConfirmationRequest(
            "session123",
            "Paris",
            true
        );
        
        Set<ConstraintViolation<DestinationConfirmationRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }
    
    @Test
    @DisplayName("Should reject DestinationConfirmationRequest with invalid destination")
    void shouldRejectDestinationConfirmationRequestWithInvalidDestination() {
        DestinationConfirmationRequest request = new DestinationConfirmationRequest(
            "session123",
            "Paris<script>alert('xss')</script>", // Invalid destination
            true
        );
        
        Set<ConstraintViolation<DestinationConfirmationRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        
        boolean hasDestinationViolation = violations.stream()
            .anyMatch(v -> v.getMessage().contains("Destination contains invalid characters"));
        assertTrue(hasDestinationViolation);
    }
    
    @Test
    @DisplayName("Should reject requests with null required fields")
    void shouldRejectRequestsWithNullRequiredFields() {
        // Test TravelDemandRequest with null fields
        TravelDemandRequest travelRequest = new TravelDemandRequest(
            null, // null destinations
            null, // null days
            null, // null passenger
            null, // null passenger type
            null, // null budgets
            null  // null user ID
        );
        
        Set<ConstraintViolation<TravelDemandRequest>> travelViolations = validator.validate(travelRequest);
        assertFalse(travelViolations.isEmpty());
        assertTrue(travelViolations.size() >= 6);
        
        // Test GuessMeRequest with null user ID
        GuessMeRequest guessRequest = new GuessMeRequest(null);
        Set<ConstraintViolation<GuessMeRequest>> guessViolations = validator.validate(guessRequest);
        assertFalse(guessViolations.isEmpty());
    }
    
    @Test
    @DisplayName("Should reject requests with blank required fields")
    void shouldRejectRequestsWithBlankRequiredFields() {
        TravelDemandRequest request = new TravelDemandRequest(
            Arrays.asList("Paris"),
            7,
            2,
            "", // blank passenger type
            "", // blank budgets
            "" // blank user ID
        );
        
        Set<ConstraintViolation<TravelDemandRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.size() >= 3);
    }
}