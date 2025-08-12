package com.pkfare.tripscale.service;

import com.pkfare.tripscale.dto.DestinationConfirmationRequest;
import com.pkfare.tripscale.dto.GuessMeRequest;
import com.pkfare.tripscale.dto.TravelDemandRequest;
import com.pkfare.tripscale.dto.TravelDetailsRequest;
import com.pkfare.tripscale.exception.RateLimitExceededException;
import com.pkfare.tripscale.exception.ValidationException;
import com.pkfare.tripscale.service.impl.TravelServiceImpl;
import com.pkfare.tripscale.util.InputSanitizer;
import com.pkfare.tripscale.util.RateLimiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for security measures in TravelService
 */
@ExtendWith(MockitoExtension.class)
class SecurityMeasuresTest {
    
    @Mock
    private DifyService difyService;
    
    @Mock
    private MemoryService memoryService;
    
    @Mock
    private TripKnowledgeService tripKnowledgeService;
    
    @Mock
    private InputSanitizer inputSanitizer;
    
    @Mock
    private RateLimiter rateLimiter;
    
    private TravelServiceImpl travelService;
    
    @BeforeEach
    void setUp() {
        travelService = new TravelServiceImpl(
            difyService, 
            memoryService, 
            tripKnowledgeService,
            inputSanitizer,
            rateLimiter
        );
    }
    
    @Test
    @DisplayName("Should sanitize input in processDirectInput")
    void shouldSanitizeInputInProcessDirectInput() {
        // Arrange
        TravelDemandRequest request = new TravelDemandRequest(
            Arrays.asList("Paris<script>"),
            7,
            2,
            "adult",
            "medium",
            "user@123"
        );
        
        when(inputSanitizer.sanitizeUserId("user@123")).thenReturn("user123");
        when(inputSanitizer.sanitizeDestinations(Arrays.asList("Paris<script>")))
            .thenReturn(Arrays.asList("Paris"));
        when(rateLimiter.isRequestAllowed(anyString(), anyString())).thenReturn(true);
        
        // Act & Assert
        assertThrows(Exception.class, () -> travelService.processDirectInput(request));
        
        // Verify sanitization was called
        verify(inputSanitizer).sanitizeUserId("user@123");
        verify(inputSanitizer).sanitizeDestinations(Arrays.asList("Paris<script>"));
    }
    
    @Test
    @DisplayName("Should check rate limits in processDirectInput")
    void shouldCheckRateLimitsInProcessDirectInput() {
        // Arrange
        TravelDemandRequest request = new TravelDemandRequest(
            Arrays.asList("Paris"),
            7,
            2,
            "adult",
            "medium",
            "user123"
        );
        
        when(inputSanitizer.sanitizeUserId("user123")).thenReturn("user123");
        when(inputSanitizer.sanitizeDestinations(Arrays.asList("Paris")))
            .thenReturn(Arrays.asList("Paris"));
        when(rateLimiter.isRequestAllowed("memory", "user123")).thenReturn(false);
        when(rateLimiter.getSecondsUntilReset("memory", "user123")).thenReturn(30L);
        
        // Act & Assert
        assertThrows(RateLimitExceededException.class, 
                    () -> travelService.processDirectInput(request));
        
        // Verify rate limit checks were called
        verify(rateLimiter).isRequestAllowed("memory", "user123");
    }
    
    @Test
    @DisplayName("Should sanitize input in initiateGuessMe")
    void shouldSanitizeInputInInitiateGuessMe() {
        // Arrange
        GuessMeRequest request = new GuessMeRequest("user@123");
        
        when(inputSanitizer.sanitizeUserId("user@123")).thenReturn("user123");
        when(rateLimiter.isRequestAllowed(anyString(), anyString())).thenReturn(true);
        
        // Act & Assert
        assertThrows(Exception.class, () -> travelService.initiateGuessMe(request));
        
        // Verify sanitization was called
        verify(inputSanitizer).sanitizeUserId("user@123");
    }
    
    @Test
    @DisplayName("Should check rate limits in initiateGuessMe")
    void shouldCheckRateLimitsInInitiateGuessMe() {
        // Arrange
        GuessMeRequest request = new GuessMeRequest("user123");
        
        when(inputSanitizer.sanitizeUserId("user123")).thenReturn("user123");
        when(rateLimiter.isRequestAllowed("memory", "user123")).thenReturn(true);
        when(rateLimiter.isRequestAllowed("dify", "user123")).thenReturn(false);
        when(rateLimiter.getSecondsUntilReset("dify", "user123")).thenReturn(45L);
        
        // Act & Assert
        assertThrows(RateLimitExceededException.class, 
                    () -> travelService.initiateGuessMe(request));
        
        // Verify rate limit checks were called
        verify(rateLimiter).isRequestAllowed("memory", "user123");
        verify(rateLimiter).isRequestAllowed("dify", "user123");
    }
    
    @Test
    @DisplayName("Should sanitize input in confirmDestination")
    void shouldSanitizeInputInConfirmDestination() {
        // Arrange
        DestinationConfirmationRequest request = new DestinationConfirmationRequest(
            "session@123",
            "Paris<script>",
            true
        );
        
        when(inputSanitizer.sanitizeSessionId("session@123")).thenReturn("session123");
        when(inputSanitizer.sanitizeDestination("Paris<script>")).thenReturn("Paris");
        
        // Act & Assert
        assertThrows(Exception.class, () -> travelService.confirmDestination(request));
        
        // Verify sanitization was called
        verify(inputSanitizer).sanitizeSessionId("session@123");
        verify(inputSanitizer).sanitizeDestination("Paris<script>");
    }
    
    @Test
    @DisplayName("Should sanitize input in collectTravelDetails")
    void shouldSanitizeInputInCollectTravelDetails() {
        // Arrange
        TravelDetailsRequest request = new TravelDetailsRequest(
            "session@123",
            7,
            2,
            "adult",
            "medium"
        );
        
        when(inputSanitizer.sanitizeSessionId("session@123")).thenReturn("session123");
        
        // Act & Assert
        assertThrows(Exception.class, () -> travelService.collectTravelDetails(request));
        
        // Verify sanitization was called
        verify(inputSanitizer).sanitizeSessionId("session@123");
    }
    
    @Test
    @DisplayName("Should sanitize session ID in getTripRoutes")
    void shouldSanitizeSessionIdInGetTripRoutes() {
        // Arrange
        String sessionId = "session@123";
        
        when(inputSanitizer.sanitizeSessionId("session@123")).thenReturn("session123");
        
        // Act & Assert
        assertThrows(Exception.class, () -> travelService.getTripRoutes(sessionId));
        
        // Verify sanitization was called
        verify(inputSanitizer).sanitizeSessionId("session@123");
    }
    
    @Test
    @DisplayName("Should throw ValidationException when sanitization fails")
    void shouldThrowValidationExceptionWhenSanitizationFails() {
        // Arrange
        TravelDemandRequest request = new TravelDemandRequest(
            Arrays.asList("Paris"),
            7,
            2,
            "adult",
            "medium",
            "invalid-user-id"
        );
        
        when(inputSanitizer.sanitizeUserId("invalid-user-id"))
            .thenThrow(new IllegalArgumentException("Invalid user ID"));
        
        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, 
                                                   () -> travelService.processDirectInput(request));
        
        assertTrue(exception.getMessage().contains("Invalid input data"));
        assertTrue(exception.getMessage().contains("Invalid user ID"));
    }
    
    @Test
    @DisplayName("Should throw RateLimitExceededException with correct details")
    void shouldThrowRateLimitExceededExceptionWithCorrectDetails() {
        // Arrange
        GuessMeRequest request = new GuessMeRequest("user123");
        
        when(inputSanitizer.sanitizeUserId("user123")).thenReturn("user123");
        when(rateLimiter.isRequestAllowed("memory", "user123")).thenReturn(true);
        when(rateLimiter.isRequestAllowed("dify", "user123")).thenReturn(true);
        when(rateLimiter.isRequestAllowed("trip-knowledge", "user123")).thenReturn(false);
        when(rateLimiter.getSecondsUntilReset("trip-knowledge", "user123")).thenReturn(60L);
        
        // Act & Assert
        RateLimitExceededException exception = assertThrows(RateLimitExceededException.class, 
                                                          () -> travelService.initiateGuessMe(request));
        
        assertEquals("trip-knowledge", exception.getService());
        assertEquals("user123", exception.getUserId());
        assertEquals(60L, exception.getRetryAfterSeconds());
    }
    
    @Test
    @DisplayName("Should check all service rate limits")
    void shouldCheckAllServiceRateLimits() {
        // Arrange
        TravelDemandRequest request = new TravelDemandRequest(
            Arrays.asList("Paris"),
            7,
            2,
            "adult",
            "medium",
            "user123"
        );
        
        when(inputSanitizer.sanitizeUserId("user123")).thenReturn("user123");
        when(inputSanitizer.sanitizeDestinations(Arrays.asList("Paris")))
            .thenReturn(Arrays.asList("Paris"));
        when(rateLimiter.isRequestAllowed(anyString(), anyString())).thenReturn(true);
        
        // Act & Assert
        assertThrows(Exception.class, () -> travelService.processDirectInput(request));
        
        // Verify all service rate limits were checked
        verify(rateLimiter).isRequestAllowed("memory", "user123");
        verify(rateLimiter).isRequestAllowed("dify", "user123");
        verify(rateLimiter).isRequestAllowed("trip-knowledge", "user123");
    }
}