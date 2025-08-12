package com.pkfare.tripscale.service.impl;

import com.pkfare.tripscale.dto.DestinationConfirmationRequest;
import com.pkfare.tripscale.dto.DestinationSuggestion;
import com.pkfare.tripscale.dto.GuessMeRequest;
import com.pkfare.tripscale.dto.GuessMeResponse;
import com.pkfare.tripscale.dto.TravelDemandRequest;
import com.pkfare.tripscale.dto.TravelDetailsRequest;
import com.pkfare.tripscale.dto.TripRoutesResponse;
import com.pkfare.tripscale.exception.BusinessException;
import com.pkfare.tripscale.exception.ResourceNotFoundException;
import com.pkfare.tripscale.exception.ValidationException;
import com.pkfare.tripscale.model.Inspirations;
import com.pkfare.tripscale.model.PersonalPreferences;
import com.pkfare.tripscale.model.TravelDemand;
import com.pkfare.tripscale.model.TripRoute;
import com.pkfare.tripscale.service.DifyService;
import com.pkfare.tripscale.service.MemoryService;
import com.pkfare.tripscale.service.TripKnowledgeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TravelServiceImplTest {
    
    @Mock
    private DifyService difyService;
    
    @Mock
    private MemoryService memoryService;
    
    @Mock
    private TripKnowledgeService tripKnowledgeService;
    
    @InjectMocks
    private TravelServiceImpl travelService;
    
    private TravelDemandRequest travelDemandRequest;
    private PersonalPreferences personalPreferences;
    private List<TripRoute> mockRoutes;
    private Inspirations mockInspirations;
    private GuessMeResponse mockGuessMeResponse;
    
    @BeforeEach
    void setUp() {
        // Setup test data
        travelDemandRequest = new TravelDemandRequest(
            Arrays.asList("Paris", "Rome"),
            7,
            2,
            "Adult",
            "Medium",
            "user123"
        );
        
        personalPreferences = new PersonalPreferences();
        personalPreferences.setLikes(Arrays.asList("Museums", "Food"));
        personalPreferences.setHates(Arrays.asList("Crowds", "Heat"));
        
        TripRoute route1 = new TripRoute();
        route1.setRouteId("route1");
        route1.setDestinations(Arrays.asList("Paris", "Rome"));
        route1.setRecommendedDays(7);
        route1.setEstimatedBudget("$2000");
        route1.setHighlights(Arrays.asList("Louvre", "Colosseum"));
        route1.setMatchScore(0.9);
        
        mockRoutes = Arrays.asList(route1);
        
        mockInspirations = new Inspirations();
        mockInspirations.setAge(30);
        mockInspirations.setTravelStyle(Arrays.asList("Cultural", "Relaxed"));
        
        DestinationSuggestion suggestion = new DestinationSuggestion();
        suggestion.setDestination("Barcelona");
        suggestion.setReason("Perfect for cultural exploration");
        suggestion.setConfidence(0.85);
        
        mockGuessMeResponse = new GuessMeResponse();
        mockGuessMeResponse.setSuggestions(Arrays.asList(suggestion));
        mockGuessMeResponse.setMessage("Here are some suggestions for you");
    }
    
    @Test
    void testProcessDirectInput_Success() {
        // Arrange
        when(memoryService.getPersonalPreferences("user123")).thenReturn(personalPreferences);
        when(tripKnowledgeService.findSuitableRoutes(any(TravelDemand.class), eq(personalPreferences)))
            .thenReturn(mockRoutes);
        
        // Act
        TripRoutesResponse response = travelService.processDirectInput(travelDemandRequest);
        
        // Assert
        assertNotNull(response);
        assertNotNull(response.getSessionId());
        assertEquals(1, response.getRoutes().size());
        assertEquals("route1", response.getRoutes().get(0).getRouteId());
        assertEquals(personalPreferences, response.getAppliedPreferences());
        assertEquals("success", response.getStatus());
        
        // Verify interactions
        verify(memoryService).getPersonalPreferences("user123");
        verify(tripKnowledgeService).findSuitableRoutes(any(TravelDemand.class), eq(personalPreferences));
        verify(memoryService).updateUserHistory(eq("user123"), any(TravelDemand.class));
    }
    
    @Test
    void testProcessDirectInput_UserNotFound() {
        // Arrange
        when(memoryService.getPersonalPreferences("user123"))
            .thenThrow(new ResourceNotFoundException("User not found"));
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            travelService.processDirectInput(travelDemandRequest);
        });
        
        verify(memoryService).getPersonalPreferences("user123");
        verifyNoInteractions(tripKnowledgeService);
    }
    
    @Test
    void testProcessDirectInput_ServiceFailure() {
        // Arrange
        when(memoryService.getPersonalPreferences("user123")).thenReturn(personalPreferences);
        when(tripKnowledgeService.findSuitableRoutes(any(TravelDemand.class), eq(personalPreferences)))
            .thenThrow(new RuntimeException("Service unavailable"));
        
        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            travelService.processDirectInput(travelDemandRequest);
        });
        
        assertTrue(exception.getMessage().contains("Failed to process direct travel input"));
    }
    
    @Test
    void testInitiateGuessMe_Success() {
        // Arrange
        GuessMeRequest request = new GuessMeRequest();
        request.setUserId("user123");
        
        when(memoryService.getInspirations("user123")).thenReturn(mockInspirations);
        when(difyService.guessDestination(mockInspirations)).thenReturn(mockGuessMeResponse);
        
        // Act
        GuessMeResponse response = travelService.initiateGuessMe(request);
        
        // Assert
        assertNotNull(response);
        assertNotNull(response.getSessionId());
        assertTrue(response.getSessionId().startsWith("session_"));
        assertEquals(1, response.getSuggestions().size());
        assertEquals("Barcelona", response.getSuggestions().get(0).getDestination());
        
        // Verify interactions
        verify(memoryService).getInspirations("user123");
        verify(difyService).guessDestination(mockInspirations);
    }
    
    @Test
    void testInitiateGuessMe_UserNotFound() {
        // Arrange
        GuessMeRequest request = new GuessMeRequest();
        request.setUserId("user123");
        
        when(memoryService.getInspirations("user123"))
            .thenThrow(new ResourceNotFoundException("User not found"));
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            travelService.initiateGuessMe(request);
        });
        
        verify(memoryService).getInspirations("user123");
        verifyNoInteractions(difyService);
    }
    
    @Test
    void testConfirmDestination_Success() {
        // Arrange - First initiate a GuessMe session
        GuessMeRequest guessMeRequest = new GuessMeRequest();
        guessMeRequest.setUserId("user123");
        
        when(memoryService.getInspirations("user123")).thenReturn(mockInspirations);
        when(difyService.guessDestination(mockInspirations)).thenReturn(mockGuessMeResponse);
        
        GuessMeResponse guessMeResponse = travelService.initiateGuessMe(guessMeRequest);
        String sessionId = guessMeResponse.getSessionId();
        
        DestinationConfirmationRequest confirmRequest = new DestinationConfirmationRequest();
        confirmRequest.setSessionId(sessionId);
        confirmRequest.setDestination("Barcelona");
        
        // Act
        String result = travelService.confirmDestination(confirmRequest);
        
        // Assert
        assertEquals(sessionId, result);
    }
    
    @Test
    void testConfirmDestination_InvalidSession() {
        // Arrange
        DestinationConfirmationRequest request = new DestinationConfirmationRequest();
        request.setSessionId("invalid_session");
        request.setDestination("Barcelona");
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            travelService.confirmDestination(request);
        });
    }
    
    @Test
    void testCollectTravelDetails_Success() {
        // Arrange - Setup complete workflow
        GuessMeRequest guessMeRequest = new GuessMeRequest();
        guessMeRequest.setUserId("user123");
        
        when(memoryService.getInspirations("user123")).thenReturn(mockInspirations);
        when(difyService.guessDestination(mockInspirations)).thenReturn(mockGuessMeResponse);
        when(memoryService.getPersonalPreferences("user123")).thenReturn(personalPreferences);
        when(tripKnowledgeService.findSuitableRoutes(any(TravelDemand.class), eq(personalPreferences)))
            .thenReturn(mockRoutes);
        
        // Initiate GuessMe
        GuessMeResponse guessMeResponse = travelService.initiateGuessMe(guessMeRequest);
        String sessionId = guessMeResponse.getSessionId();
        
        // Confirm destination
        DestinationConfirmationRequest confirmRequest = new DestinationConfirmationRequest();
        confirmRequest.setSessionId(sessionId);
        confirmRequest.setDestination("Barcelona");
        travelService.confirmDestination(confirmRequest);
        
        // Collect travel details
        TravelDetailsRequest detailsRequest = new TravelDetailsRequest();
        detailsRequest.setSessionId(sessionId);
        detailsRequest.setDays(5);
        detailsRequest.setPassenger(2);
        detailsRequest.setPassengerType("Adult");
        detailsRequest.setBudgets("Medium");
        
        // Act
        TripRoutesResponse response = travelService.collectTravelDetails(detailsRequest);
        
        // Assert
        assertNotNull(response);
        assertEquals(sessionId, response.getSessionId());
        assertEquals(1, response.getRoutes().size());
        assertEquals(personalPreferences, response.getAppliedPreferences());
        assertEquals("success", response.getStatus());
        
        // Verify interactions
        verify(memoryService).getPersonalPreferences("user123");
        verify(tripKnowledgeService).findSuitableRoutes(any(TravelDemand.class), eq(personalPreferences));
        verify(memoryService).updateUserHistory(eq("user123"), any(TravelDemand.class));
    }
    
    @Test
    void testCollectTravelDetails_InvalidSessionStatus() {
        // Arrange - Create session but don't confirm destination
        GuessMeRequest guessMeRequest = new GuessMeRequest();
        guessMeRequest.setUserId("user123");
        
        when(memoryService.getInspirations("user123")).thenReturn(mockInspirations);
        when(difyService.guessDestination(mockInspirations)).thenReturn(mockGuessMeResponse);
        
        GuessMeResponse guessMeResponse = travelService.initiateGuessMe(guessMeRequest);
        String sessionId = guessMeResponse.getSessionId();
        
        TravelDetailsRequest detailsRequest = new TravelDetailsRequest();
        detailsRequest.setSessionId(sessionId);
        detailsRequest.setDays(5);
        detailsRequest.setPassenger(2);
        detailsRequest.setPassengerType("Adult");
        detailsRequest.setBudgets("Medium");
        
        // Act & Assert
        assertThrows(ValidationException.class, () -> {
            travelService.collectTravelDetails(detailsRequest);
        });
    }
    
    @Test
    void testGetTripRoutes_Success() {
        // Arrange - Setup complete workflow
        when(memoryService.getPersonalPreferences("user123")).thenReturn(personalPreferences);
        when(tripKnowledgeService.findSuitableRoutes(any(TravelDemand.class), eq(personalPreferences)))
            .thenReturn(mockRoutes);
        
        TripRoutesResponse initialResponse = travelService.processDirectInput(travelDemandRequest);
        String sessionId = initialResponse.getSessionId();
        
        // Act
        TripRoutesResponse response = travelService.getTripRoutes(sessionId);
        
        // Assert
        assertNotNull(response);
        assertEquals(sessionId, response.getSessionId());
        assertEquals(1, response.getRoutes().size());
        assertEquals("success", response.getStatus());
    }
    
    @Test
    void testGetTripRoutes_SessionNotFound() {
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            travelService.getTripRoutes("invalid_session");
        });
    }
    
    @Test
    void testGetTripRoutes_NoRoutesAvailable() {
        // Arrange - Create session but don't process routes
        GuessMeRequest guessMeRequest = new GuessMeRequest();
        guessMeRequest.setUserId("user123");
        
        when(memoryService.getInspirations("user123")).thenReturn(mockInspirations);
        when(difyService.guessDestination(mockInspirations)).thenReturn(mockGuessMeResponse);
        
        GuessMeResponse guessMeResponse = travelService.initiateGuessMe(guessMeRequest);
        String sessionId = guessMeResponse.getSessionId();
        
        // Act & Assert
        assertThrows(ValidationException.class, () -> {
            travelService.getTripRoutes(sessionId);
        });
    }
}