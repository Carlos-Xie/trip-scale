package com.pkfare.tripscale.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pkfare.tripscale.dto.TravelDemandRequest;
import com.pkfare.tripscale.model.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
class ErrorHandlingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void directInput_WithNonexistentUser_ShouldReturn404() throws Exception {
        // Given
        TravelDemandRequest request = new TravelDemandRequest();
        request.setUserId("nonexistent");
        request.setMustGoDestinations(Arrays.asList("Paris"));
        request.setDays(5);
        request.setPassenger(2);
        request.setPassengerType("Adult");
        request.setBudgets("$2000-3000");

        // When & Then
        MvcResult result = mockMvc.perform(post("/api/travel/direct-input")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Verify error response structure
        String responseContent = result.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(responseContent, ErrorResponse.class);
        
        assertNotNull(errorResponse);
        assertEquals(404, errorResponse.getStatus());
        assertEquals("RESOURCE_NOT_FOUND", errorResponse.getErrorCode());
        assertTrue(errorResponse.getError().contains("User with identifier 'nonexistent' not found"));
        assertNotNull(errorResponse.getTimestamp());
    }

    @Test
    void directInput_WithInvalidData_ShouldReturn400() throws Exception {
        // Given - request with missing required fields
        TravelDemandRequest request = new TravelDemandRequest();
        request.setUserId(""); // Empty user ID
        request.setDays(-1); // Invalid days

        // When & Then
        mockMvc.perform(post("/api/travel/direct-input")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void directInput_WithServiceUnavailable_ShouldReturn503() throws Exception {
        // Given - request that will trigger service unavailable
        TravelDemandRequest request = new TravelDemandRequest();
        request.setUserId("testuser");
        request.setMustGoDestinations(Arrays.asList("UNAVAILABLE")); // Special trigger for service unavailable
        request.setDays(5);
        request.setPassenger(2);
        request.setPassengerType("Adult");
        request.setBudgets("$2000-3000");

        // When & Then
        MvcResult result = mockMvc.perform(post("/api/travel/direct-input")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Verify error response structure
        String responseContent = result.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(responseContent, ErrorResponse.class);
        
        assertNotNull(errorResponse);
        assertEquals(503, errorResponse.getStatus());
        assertEquals("EXTERNAL_SERVICE_ERROR", errorResponse.getErrorCode());
        assertTrue(errorResponse.getError().contains("Service temporarily unavailable"));
        assertTrue(errorResponse.getError().contains("Please try again later"));
    }

    @Test
    void directInput_WithNoRoutesFound_ShouldReturn200WithEmptyResults() throws Exception {
        // Given - request that will result in no routes found
        TravelDemandRequest request = new TravelDemandRequest();
        request.setUserId("testuser");
        request.setMustGoDestinations(Arrays.asList("UnknownDestination"));
        request.setDays(100); // Unrealistic number of days
        request.setPassenger(2);
        request.setPassengerType("Adult");
        request.setBudgets("$1");

        // When & Then
        MvcResult result = mockMvc.perform(post("/api/travel/direct-input")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Verify error response structure for no routes found
        String responseContent = result.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(responseContent, ErrorResponse.class);
        
        assertNotNull(errorResponse);
        assertEquals(200, errorResponse.getStatus());
        assertEquals("NO_ROUTES_FOUND", errorResponse.getErrorCode());
        assertTrue(errorResponse.getError().contains("No suitable routes found"));
        assertTrue(errorResponse.getError().contains("Try adjusting your search criteria"));
    }

    @Test
    void invalidEndpoint_ShouldReturn404() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/travel/nonexistent-endpoint")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void unsupportedHttpMethod_ShouldReturn405() throws Exception {
        // When & Then
        MvcResult result = mockMvc.perform(post("/api/travel/routes/session123") // This should be GET
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Verify error response structure
        String responseContent = result.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(responseContent, ErrorResponse.class);
        
        assertNotNull(errorResponse);
        assertEquals(405, errorResponse.getStatus());
        assertEquals("METHOD_NOT_ALLOWED", errorResponse.getErrorCode());
        assertTrue(errorResponse.getError().contains("HTTP method"));
        assertTrue(errorResponse.getError().contains("not supported"));
    }
}