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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class DifyServiceImplTest {
    
    @Mock
    private DifyServiceConfig config;
    
    @Mock
    private RestTemplate restTemplate;
    
    @Mock
    private ObjectMapper objectMapper;
    
    private DifyServiceImpl difyService;
    
    @BeforeEach
    void setUp() {
        lenient().when(config.getBaseUrl()).thenReturn("http://localhost:8001");
        lenient().when(config.getApiKey()).thenReturn("test-api-key");
        lenient().when(config.getMaxRetries()).thenReturn(3);
        lenient().when(config.getRetryDelayMs()).thenReturn(100);
        
        difyService = new DifyServiceImpl(restTemplate, config, objectMapper);
    }
    
    @Test
    void guessDestination_Success() throws Exception {
        // Arrange
        Inspirations inspirations = createTestInspirations();
        String mockResponseJson = """
            {
                "conversation_id": "test-session-123",
                "answer": "Based on your preferences, I recommend Tokyo, Paris, and Bali"
            }
            """;
        
        JsonNode mockJsonNode = mock(JsonNode.class);
        when(mockJsonNode.has("conversation_id")).thenReturn(true);
        when(mockJsonNode.get("conversation_id")).thenReturn(mockJsonNode);
        when(mockJsonNode.asText()).thenReturn("test-session-123");
        when(mockJsonNode.has("answer")).thenReturn(true);
        when(mockJsonNode.get("answer")).thenReturn(mockJsonNode);
        
        ResponseEntity<String> mockResponse = new ResponseEntity<>(mockResponseJson, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
            .thenReturn(mockResponse);
        when(objectMapper.readTree(mockResponseJson)).thenReturn(mockJsonNode);
        
        // Act
        GuessMeResponse result = difyService.guessDestination(inspirations);
        
        // Assert
        assertNotNull(result);
        assertEquals("test-session-123", result.getSessionId());
        assertNotNull(result.getSuggestions());
        assertFalse(result.getSuggestions().isEmpty());
        
        // Verify API call was made
        verify(restTemplate).exchange(
            eq("http://localhost:8001/v1/chat-messages"),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(String.class)
        );
    }
    
    @Test
    void guessDestination_ApiFailure_ThrowsBusinessException() {
        // Arrange
        Inspirations inspirations = createTestInspirations();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
            .thenThrow(new RestClientException("API Error"));
        
        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> difyService.guessDestination(inspirations));
        
        assertEquals("DIFY_API_ERROR", exception.getErrorCode());
        assertTrue(exception.getMessage().contains("failed after 3 attempts"));
    }
    
    @Test
    void guessDestination_RetrySuccess() throws Exception {
        // Arrange
        Inspirations inspirations = createTestInspirations();
        String mockResponseJson = """
            {
                "conversation_id": "retry-session-456",
                "answer": "Retry successful response"
            }
            """;
        
        JsonNode mockJsonNode = mock(JsonNode.class);
        when(mockJsonNode.has("conversation_id")).thenReturn(true);
        when(mockJsonNode.get("conversation_id")).thenReturn(mockJsonNode);
        when(mockJsonNode.asText()).thenReturn("retry-session-456");
        when(mockJsonNode.has("answer")).thenReturn(true);
        when(mockJsonNode.get("answer")).thenReturn(mockJsonNode);
        
        ResponseEntity<String> mockResponse = new ResponseEntity<>(mockResponseJson, HttpStatus.OK);
        
        // First call fails, second succeeds
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
            .thenThrow(new RestClientException("First attempt fails"))
            .thenReturn(mockResponse);
        when(objectMapper.readTree(mockResponseJson)).thenReturn(mockJsonNode);
        
        // Act
        GuessMeResponse result = difyService.guessDestination(inspirations);
        
        // Assert
        assertNotNull(result);
        assertEquals("retry-session-456", result.getSessionId());
        
        // Verify retry was attempted
        verify(restTemplate, times(2)).exchange(
            anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class));
    }
    
    @Test
    void guessDestination_JsonProcessingError_ThrowsBusinessException() throws Exception {
        // Arrange
        Inspirations inspirations = createTestInspirations();
        ResponseEntity<String> mockResponse = new ResponseEntity<>("invalid json", HttpStatus.OK);
        
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
            .thenReturn(mockResponse);
        when(objectMapper.readTree("invalid json"))
            .thenThrow(new RuntimeException("JSON parsing error"));
        
        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> difyService.guessDestination(inspirations));
        
        assertEquals("DIFY_API_ERROR", exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Failed to generate destination suggestions"));
    }
    
    @Test
    void isServiceHealthy_Success() {
        // Arrange
        ResponseEntity<String> mockResponse = new ResponseEntity<>("OK", HttpStatus.OK);
        when(restTemplate.exchange(
            eq("http://localhost:8001/health"), 
            eq(HttpMethod.GET), 
            any(HttpEntity.class), 
            eq(String.class)))
            .thenReturn(mockResponse);
        
        // Act
        boolean result = difyService.isServiceHealthy();
        
        // Assert
        assertTrue(result);
    }
    
    @Test
    void isServiceHealthy_Failure() {
        // Arrange
        when(restTemplate.exchange(
            eq("http://localhost:8001/health"), 
            eq(HttpMethod.GET), 
            any(HttpEntity.class), 
            eq(String.class)))
            .thenThrow(new RestClientException("Service unavailable"));
        
        // Act
        boolean result = difyService.isServiceHealthy();
        
        // Assert
        assertFalse(result);
    }
    
    @Test
    void guessDestination_WithCompleteInspirations() throws Exception {
        // Arrange
        Inspirations inspirations = createCompleteTestInspirations();
        String mockResponseJson = """
            {
                "conversation_id": "complete-session-789",
                "answer": "Complete inspirations processed successfully"
            }
            """;
        
        JsonNode mockJsonNode = mock(JsonNode.class);
        when(mockJsonNode.has("conversation_id")).thenReturn(true);
        when(mockJsonNode.get("conversation_id")).thenReturn(mockJsonNode);
        when(mockJsonNode.asText()).thenReturn("complete-session-789");
        when(mockJsonNode.has("answer")).thenReturn(true);
        when(mockJsonNode.get("answer")).thenReturn(mockJsonNode);
        
        ResponseEntity<String> mockResponse = new ResponseEntity<>(mockResponseJson, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
            .thenReturn(mockResponse);
        when(objectMapper.readTree(mockResponseJson)).thenReturn(mockJsonNode);
        
        // Act
        GuessMeResponse result = difyService.guessDestination(inspirations);
        
        // Assert
        assertNotNull(result);
        assertEquals("complete-session-789", result.getSessionId());
        assertNotNull(result.getSuggestions());
        
        // Verify that the request payload includes all inspiration data
        verify(restTemplate).exchange(
            eq("http://localhost:8001/v1/chat-messages"),
            eq(HttpMethod.POST),
            argThat(entity -> {
                @SuppressWarnings("unchecked")
                Map<String, Object> payload = (Map<String, Object>) entity.getBody();
                @SuppressWarnings("unchecked")
                Map<String, Object> inputs = (Map<String, Object>) payload.get("inputs");
                
                return inputs.containsKey("age") && 
                       inputs.containsKey("recent_focus") && 
                       inputs.containsKey("past_visits") && 
                       inputs.containsKey("travel_styles");
            }),
            eq(String.class)
        );
    }
    
    private Inspirations createTestInspirations() {
        Inspirations inspirations = new Inspirations();
        inspirations.setAge(28);
        
        RecentFocus focus1 = new RecentFocus();
        focus1.setDestination("Japan");
        focus1.setPriority(1);
        inspirations.setRecentFocus(Arrays.asList(focus1));
        
        LastVisit visit1 = new LastVisit();
        visit1.setDate("2023-06-15");
        visit1.setLocations(Arrays.asList("Paris", "London"));
        inspirations.setLast5YearVisits(Arrays.asList(visit1));
        
        inspirations.setTravelStyle(Arrays.asList("Cultural", "Adventure"));
        
        return inspirations;
    }
    
    private Inspirations createCompleteTestInspirations() {
        Inspirations inspirations = new Inspirations();
        inspirations.setAge(32);
        
        RecentFocus focus1 = new RecentFocus();
        focus1.setDestination("Southeast Asia");
        focus1.setPriority(1);
        
        RecentFocus focus2 = new RecentFocus();
        focus2.setDestination("Europe");
        focus2.setPriority(2);
        
        inspirations.setRecentFocus(Arrays.asList(focus1, focus2));
        
        LastVisit visit1 = new LastVisit();
        visit1.setDate("2023-08-20");
        visit1.setLocations(Arrays.asList("Tokyo", "Kyoto"));
        
        LastVisit visit2 = new LastVisit();
        visit2.setDate("2022-12-10");
        visit2.setLocations(Arrays.asList("Barcelona", "Madrid"));
        
        inspirations.setLast5YearVisits(Arrays.asList(visit1, visit2));
        inspirations.setTravelStyle(Arrays.asList("Cultural", "Food & Drink", "Photography"));
        
        return inspirations;
    }
}