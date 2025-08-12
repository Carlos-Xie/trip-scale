package com.pkfare.tripscale.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pkfare.tripscale.dto.*;
import com.pkfare.tripscale.model.PersonalPreferences;
import com.pkfare.tripscale.model.TripRoute;
import com.pkfare.tripscale.service.TravelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TravelController.class)
@ContextConfiguration(classes = TravelController.class)
class TravelControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private TravelService travelService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private TravelDemandRequest travelDemandRequest;
    private GuessMeRequest guessMeRequest;
    private DestinationConfirmationRequest confirmationRequest;
    private TravelDetailsRequest travelDetailsRequest;
    private TripRoutesResponse tripRoutesResponse;
    private GuessMeResponse guessMeResponse;
    
    @BeforeEach
    void setUp() {
        // Setup test data
        travelDemandRequest = new TravelDemandRequest(
            Arrays.asList("Tokyo", "Kyoto"), 7, 2, "Adult", "Medium", "user123"
        );
        
        guessMeRequest = new GuessMeRequest("user123");
        
        confirmationRequest = new DestinationConfirmationRequest("session123", "Tokyo", true);
        
        travelDetailsRequest = new TravelDetailsRequest("session123", 7, 2, "Adult", "Medium");   
     
        // Setup response objects
        List<TripRoute> routes = Arrays.asList(
            new TripRoute("route1", Arrays.asList("Tokyo", "Kyoto"), 7, "Medium", 
                         Arrays.asList("Temple visits", "Cultural experience"), 0.9)
        );
        
        PersonalPreferences preferences = new PersonalPreferences(
            Arrays.asList("Culture", "History"), Arrays.asList("Crowds")
        );
        
        tripRoutesResponse = new TripRoutesResponse("session123", routes, preferences, "success");
        
        List<DestinationSuggestion> suggestions = Arrays.asList(
            new DestinationSuggestion("Tokyo", "Based on your cultural interests", 0.9),
            new DestinationSuggestion("Kyoto", "Perfect for temple visits", 0.8)
        );
        
        guessMeResponse = new GuessMeResponse("session123", suggestions, "Here are some suggestions for you");
    }
    
    @Test
    void testDirectInput_Success() throws Exception {
        when(travelService.processDirectInput(any(TravelDemandRequest.class)))
            .thenReturn(tripRoutesResponse);
        
        mockMvc.perform(post("/api/travel/direct-input")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(travelDemandRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value("session123"))
                .andExpect(jsonPath("$.routes").isArray())
                .andExpect(jsonPath("$.routes[0].routeId").value("route1"))
                .andExpect(jsonPath("$.status").value("success"));
    }
    
    @Test
    void testDirectInput_ValidationError() throws Exception {
        TravelDemandRequest invalidRequest = new TravelDemandRequest();
        // Missing required fields
        
        mockMvc.perform(post("/api/travel/direct-input")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void testGuessMe_Success() throws Exception {
        when(travelService.initiateGuessMe(any(GuessMeRequest.class)))
            .thenReturn(guessMeResponse);
        
        mockMvc.perform(post("/api/travel/guess-me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(guessMeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value("session123"))
                .andExpect(jsonPath("$.suggestions").isArray())
                .andExpect(jsonPath("$.suggestions[0].destination").value("Tokyo"))
                .andExpect(jsonPath("$.message").value("Here are some suggestions for you"));
    }  
  
    @Test
    void testConfirmDestination_Success() throws Exception {
        when(travelService.confirmDestination(any(DestinationConfirmationRequest.class)))
            .thenReturn("session123");
        
        mockMvc.perform(post("/api/travel/confirm-destination")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(confirmationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value("session123"))
                .andExpect(jsonPath("$.status").value("confirmed"))
                .andExpect(jsonPath("$.message").value("Destination confirmed. Please provide travel details."));
    }
    
    @Test
    void testConfirmDestination_Rejected() throws Exception {
        DestinationConfirmationRequest rejectedRequest = 
            new DestinationConfirmationRequest("session123", "Tokyo", false);
        
        when(travelService.confirmDestination(any(DestinationConfirmationRequest.class)))
            .thenReturn("session123");
        
        mockMvc.perform(post("/api/travel/confirm-destination")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rejectedRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value("session123"))
                .andExpect(jsonPath("$.status").value("rejected"))
                .andExpect(jsonPath("$.message").value("Destination rejected. Please try again."));
    }
    
    @Test
    void testCollectDetails_Success() throws Exception {
        when(travelService.collectTravelDetails(any(TravelDetailsRequest.class)))
            .thenReturn(tripRoutesResponse);
        
        mockMvc.perform(post("/api/travel/collect-details")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(travelDetailsRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value("session123"))
                .andExpect(jsonPath("$.routes").isArray())
                .andExpect(jsonPath("$.status").value("success"));
    }
    
    @Test
    void testGetTripRoutes_Success() throws Exception {
        when(travelService.getTripRoutes("session123"))
            .thenReturn(tripRoutesResponse);
        
        mockMvc.perform(get("/api/travel/routes/session123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value("session123"))
                .andExpect(jsonPath("$.routes").isArray())
                .andExpect(jsonPath("$.routes[0].routeId").value("route1"))
                .andExpect(jsonPath("$.status").value("success"));
    }

}